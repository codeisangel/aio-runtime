package com.aio.runtime.mappings.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import com.aio.runtime.mappings.domain.AioMappingBo;
import com.aio.runtime.mappings.domain.AioMappingVo;
import com.aio.runtime.mappings.domain.QueryMappingParams;
import com.aio.runtime.mappings.service.IAioMappingService;
import com.aio.runtime.mappings.statistic.MappingVisitStatisticsUtils;
import com.aio.runtime.subscribe.log.SubscribeMarker;
import com.kgo.flow.common.domain.constants.ProjectWorkSpaceConstants;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.web.mappings.HandlerMethodDescription;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletMappingDescription;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletMappingDetails;
import org.springframework.boot.actuate.web.mappings.servlet.RequestMappingConditionsDescription;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lzm
 * @desc 请求映射服务实现
 * @date 2024/07/27
 */
@Service
@Slf4j
@ConditionalOnClass(MappingsEndpoint.class)
public class SqlLiteAioMappingServiceImpl implements IAioMappingService {
    @Autowired(required = false)
    private MappingsEndpoint mappingsEndpoint;

    @Value(ProjectWorkSpaceConstants.CONFIG_PATH_SPEL)
    private String projectWorkspace;
    private DataSource ds;
    private static final String TABLE_NAME = "aio_mapping";

    private static class TableFields {
        public static final String ID = "id";
        public static final String CLASS_NAME = "class_name";
        public static final String METHOD_NAME = "method_name";
        public static final String HTTP_METHOD = "http_method";
        public static final String DEPRECATED = "deprecated";
        private static final String ACTIVE_TIME = "active_time";
        private static final String VISIT_COUNTER = "visit_counter";
        public static final String URL = "url";
    }


    private void initDataSource() {
        if (!FileUtil.exist(projectWorkspace)) {
            FileUtil.mkdir(projectWorkspace);
        }
        String sqlLitePath = StrUtil.format("jdbc:sqlite:{}/aio_mapping.db", projectWorkspace);
        log.info("创建接口管理sqlLite数据源。 sqlLite数据库地址 ： {} ", sqlLitePath);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setAutoCommit(true);
        hikariDataSource.setJdbcUrl(sqlLitePath);
        // 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
        hikariDataSource.setConnectionTimeout(10 * 60 * 1000);
        // 一个连接空闲状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
        hikariDataSource.setIdleTimeout(10 * 60 * 1000);
        hikariDataSource.setPoolName("Hikari-SQLLite-4-Mapping");
        hikariDataSource.setConnectionTestQuery("SELECT 1");
        // 这里是SQLLite数据库，维护一条链接即刻，并非时排队
        hikariDataSource.setMinimumIdle(1);
        hikariDataSource.setMaximumPoolSize(1);
        ds = hikariDataSource;
        checkTable();
    }
    @Scheduled(cron="0 0/15 * * * ? ")
    public void saveMappingStatistics(){
        Set<String> mappingSet = MappingVisitStatisticsUtils.getIterator();
        if (ObjectUtil.isEmpty(mappingSet)){
            return;
        }
        for (String classAndMethod : mappingSet) {

            Integer counter = MappingVisitStatisticsUtils.getCounterAndRemove(classAndMethod);
            if (ObjectUtil.isNull(counter)){
                continue;
            }
            Date liveLyTime = MappingVisitStatisticsUtils.getLiveLyTimeAndRemove(classAndMethod);
            if (ObjectUtil.isNull(liveLyTime)){
                continue;
            }

            String className = MappingVisitStatisticsUtils.getClassName(classAndMethod);
            String methodName = MappingVisitStatisticsUtils.getMethodName(classAndMethod);
            AioMappingBo mapping = getMapping(className, methodName);
            if (ObjectUtil.isEmpty(mapping)){
                continue;
            }

            AioMappingBo updateMapping = new AioMappingBo();
            updateMapping.setId(mapping.getId());
            updateMapping.setActiveTime(liveLyTime);

            Long counter4Db = ObjectUtil.isNull(mapping.getVisitCounter()) ? 0 : mapping.getVisitCounter();
            updateMapping.setVisitCounter(counter4Db+counter);
            updateMappingById(updateMapping);

        }
    }
    @EventListener
    public void listenerApplicationReadyEvent(ApplicationReadyEvent event) {

        log.info("应用已经准备就绪-事件  读取接口信息 ： {} ", DateUtil.now());
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                initDataSource();
                clearActuateMapping();
                readMappings();
            }
        });
    }

    public void readMappings() {
        if (ObjectUtil.isNull(mappingsEndpoint)) {
            log.error(SubscribeMarker.getMarker("MappingsEndpoint")
                    , "读取接口信息失败。MappingsEndpoint 未注入。请引入 spring-boot-starter-actuator 模块，并且开启MappingsEndpoint ");
            return;
        }

        MappingsEndpoint.ApplicationMappings applicationMappings = mappingsEndpoint.mappings();
        if (ObjectUtil.isEmpty(applicationMappings)) {
            return;
        }
        Map<String, MappingsEndpoint.ContextMappings> contexts = applicationMappings.getContexts();
        if (ObjectUtil.isEmpty(contexts)) {
            return;
        }

        List<AioMappingBo> mappingBoList = new ArrayList<>();
        // 第一层 ，遍历上下文
        for (String contextName : contexts.keySet()) {
            MappingsEndpoint.ContextMappings contextMappings = contexts.get(contextName);
            if (ObjectUtil.isEmpty(contextMappings)) {
                continue;
            }

            Map<String, Object> mappings = contextMappings.getMappings();
            if (ObjectUtil.isEmpty(mappings)) {
                continue;
            }
            Object dispatcherServlets = mappings.get("dispatcherServlets");
            if (ObjectUtil.isEmpty(dispatcherServlets)) {
                continue;
            }
            Map<String, Object> servlets = (HashMap) dispatcherServlets;

            // 第二层，遍历服务
            for (String servletName : servlets.keySet()) {
                Object servletListInfo = servlets.get(servletName);

                if (ObjectUtil.isEmpty(servletListInfo)) {
                    continue;
                }

                List<Object> servletList = Convert.toList(Object.class, servletListInfo);

                for (Object servlet : servletList) {

                    if (!(servlet instanceof DispatcherServletMappingDescription)) {
                        continue;
                    }
                    DispatcherServletMappingDescription mappingDetailDesc = Convert.convert(DispatcherServletMappingDescription.class, servlet);


                    // 单个接口信息
                    DispatcherServletMappingDetails mappingDetails = mappingDetailDesc.getDetails();
                    if (ObjectUtil.isEmpty(mappingDetails)) {
                        continue;
                    }
                    HandlerMethodDescription methodDesc = mappingDetails.getHandlerMethod();
                    if (ObjectUtil.isEmpty(methodDesc)) {
                        continue;
                    }
                    RequestMappingConditionsDescription mappingDesc = mappingDetails.getRequestMappingConditions();
                    if (ObjectUtil.isEmpty(mappingDesc)) {
                        continue;
                    }

                    String className = methodDesc.getClassName();
                    String methodName = methodDesc.getName();

                    // 是否可以通过反射获取接口参数
                    boolean reflectAble = true;
                    boolean deprecated = false;
                    try {

                        Class<?> aClass = Class.forName(className);
                        // 如果类上有作废注解，表示这个接口已经作废
                        Deprecated annotation = aClass.getAnnotation(Deprecated.class);
                        if (ObjectUtil.isNotNull(annotation)) {
                            deprecated = true;
                        }
                        Method method = ReflectUtil.getMethod(aClass, methodName);
                        if (ObjectUtil.isNotNull(method)) {
                            // 如果方法上有作废注解，表示接口已作废
                            Deprecated deprecatedAnno = method.getAnnotation(Deprecated.class);
                            if (ObjectUtil.isNotNull(deprecatedAnno)) {
                                deprecated = true;
                            }

                        }


                    } catch (ClassNotFoundException e) {
                        if (!StringUtils.contains(className,"org.springframework.boot.actuate.endpoint.web.servlet")){
                            log.error("读取所有接口,获取接口实现类[ {} ]的类类型时，类未查询到。", className);
                        }
                        reflectAble = false;
                    } catch (SecurityException e) {
                        log.error("读取所有接口,获取接口实现类[ {} ]的类类型时，类未查询到或方法[ {} ]未查询到。 ", className, methodName);
                        reflectAble = false;
                    }


                    Set<RequestMethod> methods = mappingDesc.getMethods();
                    Set<String> urlList = mappingDesc.getPatterns();
                    if (ObjectUtil.isEmpty(urlList)) {
                        continue;
                    }
                    for (String url : urlList) {
                        AioMappingBo mappingBo = new AioMappingBo();
                        mappingBo.setId(IdUtil.getSnowflakeNextIdStr());
                        mappingBo.setDeprecated(false);
                        mappingBo.setClassName(className);
                        mappingBo.setMethodName(methodName);
                        mappingBo.setHttpMethod(methods.toString());
                        mappingBo.setDeprecated(deprecated);
                        mappingBo.setUrl(url);
                        mappingBoList.add(mappingBo);
                    }

                }

            }

        }
        batchSave(mappingBoList);

    }

    @Override
    public PageResult<AioMappingVo> getPage(QueryMappingParams params, KgoPage page) {
        try {
            Entity where = Entity.create(TABLE_NAME);

            if (StringUtils.isNotBlank(params.getClassName())) {
                params.setClassName(StringUtils.trim(params.getClassName()));
                where.set(TableFields.CLASS_NAME, StrUtil.format("like %{}%", params.getClassName()));
            }
            if (StringUtils.isNotBlank(params.getMethodName())) {
                params.setMethodName(StringUtils.trim(params.getMethodName()));
                where.set(TableFields.METHOD_NAME, StrUtil.format("like %{}%", params.getMethodName()));
            }
            if (StringUtils.isNotBlank(params.getHttpMethod())) {
                params.setHttpMethod(StringUtils.trim(params.getHttpMethod()));
                where.set(TableFields.HTTP_METHOD, StrUtil.format("like %{}%", params.getHttpMethod()));
            }
            if (StringUtils.isNotBlank(params.getUrl())) {
                where.set(TableFields.URL, StrUtil.format("like %{}%", params.getUrl()));
            }
            if (ObjectUtil.isNotNull(params.getDeprecated())) {
                where.set(TableFields.DEPRECATED, params.getDeprecated());
            }

            Page hutoolPage = new Page(page.getPageNum() - 1, page.getPageSize());
            cn.hutool.db.PageResult<Entity> hutoolPageResult = Db.use(ds).page(where, hutoolPage);

            List<AioMappingVo> mappingVoList = new ArrayList<>();
            for (Entity entity : hutoolPageResult) {
                AioMappingVo mappingVo = entity.toBean(AioMappingVo.class);
                mappingVoList.add(mappingVo);
            }

            PageResult<AioMappingVo> pageResult = new PageResult<>();
            pageResult.setList(mappingVoList);
            pageResult.setTotal(hutoolPageResult.getTotal());

            return pageResult;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AioMappingBo getMapping(String className, String methodName) {
        try {
            Entity where = Entity.create(TABLE_NAME)
                    .set(TableFields.CLASS_NAME,className)
                    .set(TableFields.METHOD_NAME,methodName);
            List<Entity> entities = Db.use(ds).find(where);
            if (ObjectUtil.isEmpty(entities)){
                return null;
            }
            if (entities.size()<1) {
                return null;
            }
            return entities.get(0).toBean(AioMappingBo.class);
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateMappingById(AioMappingBo mappingBo) {
        try {
            long start = System.currentTimeMillis();
            Entity where = Entity.create(TABLE_NAME).set(TableFields.ID,mappingBo.getId());
            Entity entity = new Entity();
            if (ObjectUtil.isNotNull(mappingBo.getActiveTime())){
                entity.put(TableFields.ACTIVE_TIME,mappingBo.getActiveTime());
            }
            if (ObjectUtil.isNotNull(mappingBo.getVisitCounter())){
                entity.put(TableFields.VISIT_COUNTER,mappingBo.getVisitCounter());
            }
            if (ObjectUtil.isNotNull(mappingBo.getDeprecated())){
                entity.put(TableFields.DEPRECATED,mappingBo.getDeprecated());
            }
            if (StringUtils.isNotBlank(mappingBo.getUrl())){
                entity.put(TableFields.URL,mappingBo.getUrl());
            }
            if (StringUtils.isNotBlank(mappingBo.getHttpMethod())){
                entity.put(TableFields.HTTP_METHOD,mappingBo.getHttpMethod());
            }

            int update = Db.use(ds).update(entity, where);
            log.debug("更新接口 : ID[ {} ] 结果 {} , 耗时[ {} ] ",mappingBo.getId(),update, (System.currentTimeMillis() -start ));
            return update > 0;
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void batchSave(List<AioMappingBo> mappingBoList) {
        if (ObjectUtil.isEmpty(mappingBoList)) {
            return;
        }
        log.info("插入系统接口信息，数量 : {} 条。", mappingBoList.size());
        long start = System.currentTimeMillis();

        AtomicInteger addCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        for (AioMappingBo itemBo : mappingBoList) {
            if (StringUtils.contains(itemBo.getClassName(),"org.springframework.boot.actuate.endpoint.web.servlet")){
                if (addMapping(itemBo)) {
                    addCount.addAndGet(1);
                }
            }else {
                if (updateMapping(itemBo)) {
                    updateCount.addAndGet(1);
                }else {
                    if (addMapping(itemBo)) {
                        addCount.addAndGet(1);
                    }
                }
            }

        }
        log.info("插入系统接口信息 ：共 {} 条  插入成功[ {} ] 条 ，其中更新[ {} ] 条, 新增[ {} ] 条， 耗时[ {} ] "
                , mappingBoList.size(), updateCount.get()+addCount.get(), updateCount.get(), addCount.get(), (System.currentTimeMillis() - start));
    }
    private boolean updateMapping(AioMappingBo itemBo){
        AioMappingBo mapping = getMapping(itemBo.getClassName(), itemBo.getMethodName());
        if (ObjectUtil.isNull(mapping)){
            return false;
        }
        AioMappingBo updateMapping = new AioMappingBo();
        updateMapping.setId(mapping.getId());
        Long counter4Db = ObjectUtil.isNull(mapping.getVisitCounter()) ? 0 : mapping.getVisitCounter();
        updateMapping.setVisitCounter(counter4Db);
        updateMapping.setUrl(itemBo.getUrl());
        updateMapping.setDeprecated(itemBo.getDeprecated());
        updateMapping.setHttpMethod(itemBo.getHttpMethod());
        return updateMappingById(updateMapping);
    }
    private boolean addMapping(AioMappingBo itemBo){
        Entity entity = Entity.create(TABLE_NAME)
                .set(TableFields.ID, itemBo.getId())
                .set(TableFields.CLASS_NAME, itemBo.getClassName())
                .set(TableFields.METHOD_NAME, itemBo.getMethodName())
                .set(TableFields.URL, itemBo.getUrl())
                .set(TableFields.HTTP_METHOD, itemBo.getHttpMethod())
                .set(TableFields.DEPRECATED, itemBo.getDeprecated());
        try {
            int insert = Db.use(ds).insert(entity);
            return insert > 0 ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void clearActuateMapping(){
        try {
            Entity where = Entity.create(TABLE_NAME)
                    .set(TableFields.CLASS_NAME,StrUtil.format("like {}%", "org.springframework.boot.actuate.endpoint.web.servlet"));
            int del = Db.use(ds).del(where);
            log.info("清理ActuateMapping 数据，删除数量[ {} ]",del);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void checkTable() {
        try {
            Entity entity = Db.use(ds).queryOne("SELECT * FROM sqlite_master WHERE type='table' AND name= ? ", TABLE_NAME);
            if (ObjectUtil.isNull(entity)) {
                StringBuffer sql = new StringBuffer();
                sql.append(String.format("CREATE TABLE '%s' (", TABLE_NAME));
                sql.append("  \"id\" text NOT NULL,");
                sql.append("  \"class_name\" text,");
                sql.append("  \"method_name\" text,");
                sql.append("  \"http_method\" text,");
                sql.append("  \"deprecated\" integer,");
                sql.append("  \"active_time\" integer,");
                sql.append("  \"visit_counter\" integer,");
                sql.append("  \"url\" text,");
                sql.append("  PRIMARY KEY (\"id\") )");
                int execute = Db.use(ds).execute(sql.toString());
            } else {
                // 清空表
                //Db.use(ds).execute("DELETE FROM aio_mapping ");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
