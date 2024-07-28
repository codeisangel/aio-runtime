package com.aio.runtime.environment.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import com.aio.runtime.environment.domain.EnvironmentItemBo;
import com.aio.runtime.environment.domain.EnvironmentItemDictBo;
import com.aio.runtime.environment.domain.QueryEnvironmentParams;
import com.aio.runtime.environment.service.IAioEnvironmentService;
import com.aio.runtime.subscribe.log.SubscribeMarker;
import com.alibaba.fastjson.JSON;
import com.kgo.flow.common.domain.constants.ProjectWorkSpaceConstants;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lzm
 * @desc SqlLite环境存储
 * @date 2024/07/26
 */
@Service
@Slf4j
public class SqlLiteAioEnvironmentServiceImpl implements IAioEnvironmentService {
    private static class TableFields{
        public static final String ID = "id";
        public static final String ENVIRONMENT_GROUP = "environment_group";
        public static final String PROPERTY_KEY = "property_key";
        public static final String PROPERTY_VALUE = "property_value";
        public static final String PROPERTY_DESC = "property_desc";
        public static final String PROPERTY_TYPE = "property_type";
    }
    private DataSource ds;
    private static final String TABLE_NAME = "aio_environment";
    @Value(ProjectWorkSpaceConstants.CONFIG_PATH_SPEL)
    private String projectWorkspace;

    @Value("classpath:material/environment/environmentItem.json")
    private Resource environmentItemFile;

    @Autowired(required = false)
    private EnvironmentEndpoint environmentEndpoint;

    private Map<String,EnvironmentItemDictBo> environmentDictMap = new HashMap<>();
    @EventListener
    public void listenerApplicationReadyEvent(ApplicationReadyEvent event){
        log.info("应用已经准备就绪-事件 ： {} ", DateUtil.now());
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                // 读取字典
                readDict();
                // 初始化数据源
                initDataSource();
                // 读取环境配置
                readEnvironments();
            }
        });

    }

    private void readDict(){
        try {
            File file = environmentItemFile.getFile();
            String environmentDict = FileUtil.readUtf8String(file);
            if (StringUtils.isBlank(environmentDict)){
                return;
            }
            List<EnvironmentItemDictBo> dictList = JSON.parseArray(environmentDict, EnvironmentItemDictBo.class);
            log.info("字典内容 ： {} ", JSON.toJSONString(dictList));
            if (ObjectUtil.isEmpty(dictList)){
                return;
            }
            for (EnvironmentItemDictBo dictBo : dictList) {
                if (ObjectUtil.isEmpty(dictBo)){
                    continue;
                }
                if (StringUtils.isBlank(dictBo.getKey())) {
                    continue;
                }
                environmentDictMap.put(dictBo.getKey(),dictBo);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    private void initDataSource(){
        if (!FileUtil.exist(projectWorkspace)) {
            FileUtil.mkdir(projectWorkspace);
        }
        String sqlLitePath = StrUtil.format("jdbc:sqlite:{}/aio_environment.db",projectWorkspace);
        log.info("创建环境sqlLite数据源。 sqlLite数据库地址 ： {} ",sqlLitePath);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setAutoCommit(true);
        hikariDataSource.setJdbcUrl(sqlLitePath);
        // 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
        hikariDataSource.setConnectionTimeout(10 * 60 * 1000);
        // 一个连接空闲状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
        hikariDataSource.setIdleTimeout(10 * 60 * 1000);
        hikariDataSource.setPoolName("Hikari-SQLLite-4-Environment");
        hikariDataSource.setConnectionTestQuery("SELECT 1");
        // 这里是SQLLite数据库，维护一条链接即刻，并非时排队
        hikariDataSource.setMinimumIdle(1);
        hikariDataSource.setMaximumPoolSize(1);
        ds = hikariDataSource;
        checkTable();
    }
    @Override
    public PageResult<EnvironmentItemBo> getPage(QueryEnvironmentParams params, KgoPage page) {
        try {
            Entity where = Entity.create(TABLE_NAME);

            if (StringUtils.isNotBlank(params.getEnvironmentGroup())){
                where.set(TableFields.ENVIRONMENT_GROUP,StrUtil.format("like %{}%",params.getEnvironmentGroup()));
            }
            if (StringUtils.isNotBlank(params.getPropertyKey())){
                where.set(TableFields.PROPERTY_KEY,StrUtil.format("like %{}%",params.getPropertyKey()));
            }
            if (StringUtils.isNotBlank(params.getPropertyValue())){
                where.set(TableFields.PROPERTY_VALUE,StrUtil.format("like %{}%",params.getPropertyValue()));
            }
            if (StringUtils.isNotBlank(params.getPropertyDesc())){

                where.set(TableFields.PROPERTY_DESC,StrUtil.format("like %{}%",params.getPropertyDesc()));
            }

            cn.hutool.db.PageResult<Entity> hutoolPageResult = Db.use(ds).page(
                    where
                    , new Page(page.getPageNum()-1, page.getPageSize()));

            List<EnvironmentItemBo> environmentItemBoList = new ArrayList<>();
            for (Entity entity : hutoolPageResult) {
                EnvironmentItemBo logBo = entity.toBean(EnvironmentItemBo.class);
                environmentItemBoList.add(logBo);
            }

            PageResult<EnvironmentItemBo> pageResult = new PageResult<>();
            pageResult.setList(environmentItemBoList);
            pageResult.setTotal(hutoolPageResult.getTotal());

            return pageResult;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readEnvironments(){
        if (ObjectUtil.isNull(environmentEndpoint)){
            log.error(SubscribeMarker.getMarker("environmentEndpoint")
                    ,"读取环境信息失败。EnvironmentEndpoint 未注入。请引入 spring-boot-starter-actuator 模块，并且开启EnvironmentEndpoint ");
            return;
        }
        EnvironmentEndpoint.EnvironmentDescriptor environment = environmentEndpoint.environment(null);
        List<EnvironmentEndpoint.PropertySourceDescriptor> propertySources = environment.getPropertySources();
        if (ObjectUtil.isEmpty(propertySources)){
            return;
        }
        List<EnvironmentItemBo> itemBoList = new ArrayList<>();
        for (EnvironmentEndpoint.PropertySourceDescriptor propertySource : propertySources) {

            String environmentGroup = propertySource.getName();

            Map<String, EnvironmentEndpoint.PropertyValueDescriptor> properties = propertySource.getProperties();
            if (ObjectUtil.isEmpty(properties)){
                continue;
            }
            for (String propertyKey : properties.keySet()) {
                EnvironmentEndpoint.PropertyValueDescriptor propertyValueDescriptor = properties.get(propertyKey);
                if (ObjectUtil.isNull(propertyValueDescriptor)){
                    continue;
                }
                Object value = propertyValueDescriptor.getValue();
                if (ObjectUtil.isNull(value)){
                    continue;
                }
                EnvironmentItemBo itemBo = new EnvironmentItemBo();
                itemBo.setId(IdUtil.getSnowflakeNextIdStr());
                itemBo.setEnvironmentGroup(environmentGroup);
                itemBo.setPropertyKey(propertyKey);
                itemBo.setPropertyValue(value.toString());
                itemBo.setPropertyType(value.getClass().getSimpleName());
                itemBoList.add(itemBo);
            }
        }
        batchSave(itemBoList);


    }
    public boolean updateProperty(EnvironmentItemBo itemBo){
        try {
            EnvironmentItemDictBo dictBo = environmentDictMap.get(itemBo.getPropertyKey());
            Entity entity = Entity.create()
                    .set("property_value", itemBo.getPropertyValue())
                    .set("property_desc", itemBo.getPropertyDesc())
                    .set("property_type", itemBo.getPropertyType());

            if (ObjectUtil.isNotEmpty(dictBo)){
                entity.set(TableFields.PROPERTY_DESC,dictBo.getName());
            }

            Entity where = Entity.create(TABLE_NAME)
                    .set("environment_group", itemBo.getEnvironmentGroup())
                    .set("property_key", itemBo.getPropertyKey());

            int update = Db.use(ds).update(entity, where);
            return update > 0 ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addProperty(EnvironmentItemBo itemBo){
        try {
            Entity entity = Entity.create(TABLE_NAME)
                    .set("id", itemBo.getId())
                    .set("environment_group", itemBo.getEnvironmentGroup())
                    .set("property_key", itemBo.getPropertyKey())
                    .set("property_value", itemBo.getPropertyValue())
                    .set("property_desc", itemBo.getPropertyDesc())
                    .set("property_type", itemBo.getPropertyType());

            int insert = Db.use(ds).insert(entity);
            return insert > 0 ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public void batchSave(List<EnvironmentItemBo> recordBos) {
        if (ObjectUtil.isEmpty(recordBos)){
            return;
        }
        log.info("插入环境信息，数量 : {} 条。",recordBos.size());
        long start = System.currentTimeMillis();
        AtomicInteger updateCount = new AtomicInteger(0);
        AtomicInteger addCount = new AtomicInteger(0);
        for (EnvironmentItemBo recordBo : recordBos) {
            if (updateProperty(recordBo)) {
                updateCount.addAndGet(1);
            }else {
                if (addProperty(recordBo)) {
                    addCount.addAndGet(1);
                }
            }

        }
        log.info("插入环境信息 ：共 {} 条  插入成功[ {} ] 条 ，其中 更新[ {} ] 条， 新增[ {} ] 条 ， 耗时[ {} ] "
                ,recordBos.size(),updateCount.get()+addCount.get(),updateCount.get(),addCount.get(),(System.currentTimeMillis()-start));
    }

    private void checkTable(){
        try {
            Entity entity = Db.use(ds).queryOne("SELECT * FROM sqlite_master WHERE type='table' AND name= ? ", TABLE_NAME);
            if (ObjectUtil.isNull(entity)){
                StringBuffer sql = new StringBuffer();
                sql.append(String.format("CREATE TABLE '%s' (",TABLE_NAME));
                sql.append("  \"id\" text NOT NULL,");
                sql.append("  \"environment_group\" text,");
                sql.append("  \"property_key\" text,");
                sql.append("  \"property_value\" text,");
                sql.append("  \"property_desc\" text,");
                sql.append("  \"property_type\" text,");
                sql.append("  PRIMARY KEY (\"id\") )");
                int execute = Db.use(ds).execute(sql.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
