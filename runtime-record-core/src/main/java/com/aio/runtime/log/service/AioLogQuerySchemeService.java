package com.aio.runtime.log.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aio.runtime.db.orm.conditions.AioQueryCondition;
import com.aio.runtime.db.orm.service.AbstractAioSimpleMapper;
import com.aio.runtime.log.domain.AioLogParamsBo;
import com.kgo.flow.common.domain.constants.ProjectWorkSpaceConstants;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lzm
 * @desc 日志查询
 * @date 2024/08/09
 */
@Service
@Slf4j
public class AioLogQuerySchemeService extends AbstractAioSimpleMapper<AioLogParamsBo> {
    @Value(ProjectWorkSpaceConstants.CONFIG_PATH_SPEL)
    private String projectWorkspace;

    public List<AioLogParamsBo> getList(){
        AioQueryCondition<AioLogParamsBo> conditions = new AioQueryCondition();
        KgoPage page = new KgoPage();
        page.setPageSize(1000);
        PageResult<AioLogParamsBo> pageResult = getPage(conditions, page);
        if (ObjectUtil.isNull(pageResult)){
            return new ArrayList<>();
        }
        return pageResult.getList();
    }
    public void saveScheme(AioLogParamsBo aioLogParamsBo){
        if (ObjectUtil.isNull(aioLogParamsBo)){
            return;
        }
        aioLogParamsBo.setUpdateTime(new Date());
        if (StringUtils.isBlank(aioLogParamsBo.getId())){
            aioLogParamsBo.setId(IdUtil.getSnowflakeNextIdStr());
            addRow(aioLogParamsBo);
        }else {
            updateById(aioLogParamsBo);
        }
    }
    private void initDataSource() {
        if (!FileUtil.exist(projectWorkspace)) {
            FileUtil.mkdir(projectWorkspace);
        }
        String indexPath = StrUtil.format("{}/log/", projectWorkspace);
        if (!FileUtil.exist(indexPath)){
            FileUtil.mkdir(indexPath);
        }
        String sqlLitePath = StrUtil.format("jdbc:sqlite:{}/log/aio_log_scheme.db", projectWorkspace);
        log.info("创建日志查询策略 sqlLite数据源。 sqlLite数据库地址 ： {} ", sqlLitePath);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setAutoCommit(true);
        hikariDataSource.setJdbcUrl(sqlLitePath);
        // 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
        hikariDataSource.setConnectionTimeout(10 * 60 * 1000);
        // 一个连接空闲状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
        hikariDataSource.setIdleTimeout(10 * 60 * 1000);
        hikariDataSource.setPoolName("Hikari-SQLLite-4-LOG-SCHEME");
        hikariDataSource.setConnectionTestQuery("SELECT 1");
        // 这里是SQLLite数据库，维护一条链接即刻，并非时排队
        hikariDataSource.setMinimumIdle(1);
        hikariDataSource.setMaximumPoolSize(1);
        setDataSource(hikariDataSource);
    }
    @EventListener
    public void listenerApplicationReadyEvent(ApplicationReadyEvent event) {
        log.info("应用已经准备就绪-事件 日志查询方案SQL  ： {} ", DateUtil.now());
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                initDataSource();
                getTableColumns();
                checkTable();
            }
        });
    }
}
