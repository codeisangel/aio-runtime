package com.aio.runtime.record.log.subscribe.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.sql.Direction;
import cn.hutool.db.sql.Order;
import com.aio.runtime.record.log.subscribe.domain.SubscribeLogBo;
import com.aio.runtime.record.log.subscribe.domain.SubscribeLogVo;
import com.aio.runtime.record.log.subscribe.domain.params.QuerySubscribeLogParams;
import com.aio.runtime.record.log.subscribe.service.AbstractSubscribeLogService;
import com.kgo.flow.common.domain.constants.ProjectWorkSpaceConstants;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lzm
 * @desc 订阅日志SQLlite存储方案
 * @date 2024/07/24
 */
@Service
@Slf4j
public class SqlLiteSubscribeLogServiceImpl extends AbstractSubscribeLogService {
    private static class SubscribeFields{
        public static final String ID = "id";
        public static final String CREATE_TIME = "create_time";
        public static final String HANDLE_TIME = "handle_time";
        public static final String HANDLE_STATUS = "handle_status";
        public static final String SUBSCRIBE_NAME = "subscribe_name";
        public static final String USER_ID = "user_id";
        public static final String COMPANY_ID = "company_id";
        public static final String TRACE_ID = "trace_id";
        public static final String CLASS_NAME = "class_name";
        public static final String METHOD_NAME = "method_name";
        public static final String MESSAGE = "message";

    }
    private static final String TABLE_NAME = "subscribe_log_record";
    private DataSource ds;
    public static final String SUBSCRIBE_LOG_CATALOGUE_NAME = "subscribeLog";
    @Value(ProjectWorkSpaceConstants.CONFIG_PATH_SPEL)
    private String projectWorkspace;
    /**
     *
     */
    @PostConstruct
    private void initDataSource(){
        String sqlLiteCataloguePath = StrUtil.format("{}/{}", projectWorkspace, SUBSCRIBE_LOG_CATALOGUE_NAME);
        if (!FileUtil.exist(sqlLiteCataloguePath)) {
            FileUtil.mkdir(sqlLiteCataloguePath);
        }
        String sqlLitePath = StrUtil.format("jdbc:sqlite:{}/subscribeLog.db",sqlLiteCataloguePath);
        log.info("创建日志订阅sqlLite数据源。 sqlLite数据库地址 ： {} ",sqlLitePath);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setAutoCommit(true);
        hikariDataSource.setJdbcUrl(sqlLitePath);
        // 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
        hikariDataSource.setConnectionTimeout(10 * 60 * 1000);
        // 一个连接空闲状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
        hikariDataSource.setIdleTimeout(10 * 60 * 1000);
        hikariDataSource.setConnectionTestQuery("SELECT 1");
        // 这里是SQLLite数据库，维护一条链接即刻，并非时排队
        hikariDataSource.setMinimumIdle(1);
        hikariDataSource.setMaximumPoolSize(1);
        ds = hikariDataSource;
        checkTable();
    }
    @Override
    public void batchSave(List<SubscribeLogBo> recordBos) {
        if (ObjectUtil.isEmpty(recordBos)){
            return;
        }
        List<Entity> recordList = new ArrayList<>();
        for (SubscribeLogBo recordBo : recordBos) {
            Entity entity = Entity.create(TABLE_NAME)
                    .set(SubscribeFields.ID, recordBo.getId())
                    .set(SubscribeFields.CREATE_TIME, recordBo.getCreateTime())
                    .set(SubscribeFields.SUBSCRIBE_NAME, recordBo.getSubscribeName())
                    .set(SubscribeFields.USER_ID, recordBo.getUserId())
                    .set(SubscribeFields.COMPANY_ID, recordBo.getCompanyId())
                    .set(SubscribeFields.TRACE_ID, recordBo.getTraceId())
                    .set(SubscribeFields.CLASS_NAME, recordBo.getClassName())
                    .set(SubscribeFields.METHOD_NAME, recordBo.getMethodName())
                    .set(SubscribeFields.HANDLE_TIME, recordBo.getHandleTime())
                    .set(SubscribeFields.HANDLE_STATUS, recordBo.getHandleStatus())
                    .set(SubscribeFields.MESSAGE, recordBo.getMessage());
            recordList.add(entity);
        }

        try {
            log.info("插入订阅日志记录数量 : {} 条。",recordList.size());
            long start = System.currentTimeMillis();
            int[] insert = Db.use(ds).insert(recordList);
            log.info("插入订阅记录 ： {} 条  耗时[ {} ] ",insert.length,(System.currentTimeMillis()-start));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageResult<SubscribeLogVo> getPage(QuerySubscribeLogParams params, KgoPage page) {
        try {
            Entity where = Entity.create(TABLE_NAME);

            if (StringUtils.isNotBlank(params.getSubscribeName())){
                where.set(SubscribeFields.SUBSCRIBE_NAME,StrUtil.format("like %{}%",params.getSubscribeName()));
            }
            if (StringUtils.isNotBlank(params.getClassName())){
                where.set(SubscribeFields.CLASS_NAME, StrUtil.format("like %{}%",params.getClassName()));
            }
            if (StringUtils.isNotBlank(params.getMessage())){
                where.set(SubscribeFields.MESSAGE,StrUtil.format("like %{}%",params.getMessage()));
            }
            if (StringUtils.isNotBlank(params.getMethodName())){
                where.set(SubscribeFields.METHOD_NAME,StrUtil.format("like %{}%",params.getMethodName()));
            }

            if (StringUtils.isNotBlank(params.getUserId())){
                where.set(SubscribeFields.USER_ID,params.getUserId());
            }

            if (StringUtils.isNotBlank(params.getCompanyId())){
                where.set(SubscribeFields.COMPANY_ID,params.getCompanyId());
            }

            if (ObjectUtil.isAllNotEmpty(params.getCreateFromTime(),params.getCreateToTime())){
                where.set(SubscribeFields.CREATE_TIME,StrUtil.format("BETWEEN {} AND {}",params.getCreateFromTime(),params.getCreateToTime()));
            }else {
                if (ObjectUtil.isNotNull(params.getCreateFromTime())){
                    where.set(SubscribeFields.CREATE_TIME,StrUtil.format("> {}",params.getCreateFromTime()));
                }
                if (ObjectUtil.isNotNull(params.getCreateToTime())){
                    where.set(SubscribeFields.CREATE_TIME,StrUtil.format("< {}",params.getCreateToTime()));
                }
            }

            Order createTimeOrder = new Order(SubscribeFields.CREATE_TIME, Direction.DESC);
            Order handleTimeOrder = new Order(SubscribeFields.CREATE_TIME, Direction.DESC);
            Page hutoolPage = new Page(page.getPageNum() - 1, page.getPageSize());
            hutoolPage.setOrder(createTimeOrder,handleTimeOrder);

            cn.hutool.db.PageResult<Entity> hutoolPageResult = Db.use(ds).page(
                     where
                    , hutoolPage);

            List<SubscribeLogVo> subscribeLogBoList = new ArrayList<>();
            for (Entity entity : hutoolPageResult) {
                SubscribeLogVo logBo = entity.toBean(SubscribeLogVo.class);
                subscribeLogBoList.add(logBo);
            }

            PageResult<SubscribeLogVo> pageResult = new PageResult<>();
            pageResult.setList(subscribeLogBoList);
            pageResult.setTotal(hutoolPageResult.getTotal());

            return pageResult;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void checkTable(){
        try {
            Entity entity = Db.use(ds).queryOne("SELECT * FROM sqlite_master WHERE type='table' AND name= ? ", TABLE_NAME);
            if (ObjectUtil.isNull(entity)){
                StringBuffer sql = new StringBuffer();
                sql.append("CREATE TABLE \"subscribe_log_record\" (");
                sql.append("  \"id\" text NOT NULL,");
                sql.append("  \"create_time\" integer,");
                sql.append("  \"subscribe_name\" text,");
                sql.append("  \"user_id\" text,");
                sql.append("  \"company_id\" text,");
                sql.append("  \"trace_id\" text,");
                sql.append("  \"class_name\" text,");
                sql.append("  \"method_name\" text,");
                sql.append("  \"handle_time\" integer,");
                sql.append("  \"handle_status\" integer,");
                sql.append("  \"message\" text,");
                sql.append("  PRIMARY KEY (\"id\") )");
                int execute = Db.use(ds).execute(sql.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
