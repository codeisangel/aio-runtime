package com.aio.runtime.beans.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aio.runtime.beans.domain.AioBeanBo;
import com.aio.runtime.beans.domain.AioBeanVo;
import com.aio.runtime.beans.domain.QueryBeanParams;
import com.aio.runtime.beans.service.IAioBeansService;
import com.aio.runtime.db.orm.conditions.AioQueryCondition;
import com.aio.runtime.db.orm.service.AbstractAioSimpleMapper;
import com.aio.runtime.subscribe.log.SubscribeMarker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kgo.flow.common.domain.constants.ProjectWorkSpaceConstants;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lzm
 * @desc beans服务SqlLite存储
 * @date 2024/07/31
 */
@Service
@Slf4j
@ConditionalOnClass(BeansEndpoint.class)
public class SqlLiteBeansServiceImpl extends AbstractAioSimpleMapper<AioBeanBo> implements IAioBeansService {
    @Autowired(required = false)
    private BeansEndpoint beansEndpoint;

    @Value(ProjectWorkSpaceConstants.CONFIG_PATH_SPEL)
    private String projectWorkspace;

    @Override
    public PageResult<AioBeanVo> getPage(QueryBeanParams params, KgoPage page) {
        AioQueryCondition<AioBeanBo> conditions = new AioQueryCondition<>();
        if (StringUtils.isNotBlank(params.getClassName())) {
            conditions.like(AioBeanBo::getClassName, params.getClassName());
        }
        if (StringUtils.isNotBlank(params.getBeanName())) {
            conditions.like(AioBeanBo::getBeanName, params.getBeanName());
        }
        if (StringUtils.isNotBlank(params.getAliases())) {
            conditions.like(AioBeanBo::getAliases, params.getAliases());
        }
        if (StringUtils.isNotBlank(params.getInterfaceName())) {
            conditions.like(AioBeanBo::getInterfaces, params.getInterfaceName());
        }
        if (StringUtils.isNotBlank(params.getSuperclass())) {
            conditions.like(AioBeanBo::getSuperclass, params.getSuperclass());
        }
        PageResult<AioBeanBo> pageResultBo = getPage(conditions, page);
        List<AioBeanVo> beanVoList = new ArrayList<>();
        for (AioBeanBo beanBo : pageResultBo.getList()) {
            AioBeanVo aioBeanVo = Convert.convert(AioBeanVo.class, beanBo);
            aioBeanVo.setAliases(JSONObject.parseArray(beanBo.getAliases(), String.class));
            aioBeanVo.setDependencies(JSONObject.parseArray(beanBo.getDependencies(), String.class));
            aioBeanVo.setInterfaces(JSONObject.parseArray(beanBo.getInterfaces(), String.class));
            beanVoList.add(aioBeanVo);
        }
        PageResult<AioBeanVo> pageResult = new PageResult<>(beanVoList, pageResultBo.getTotal());
        return pageResult;
    }

    private void initDataSource() {
        if (!FileUtil.exist(projectWorkspace)) {
            FileUtil.mkdir(projectWorkspace);
        }
        String sqlLitePath = StrUtil.format("jdbc:sqlite:{}/aio_beans.db", projectWorkspace);
        log.info("创建BEAN sqlLite数据源。 sqlLite数据库地址 ： {} ", sqlLitePath);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setAutoCommit(true);
        hikariDataSource.setJdbcUrl(sqlLitePath);
        // 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
        hikariDataSource.setConnectionTimeout(10 * 60 * 1000);
        // 一个连接空闲状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
        hikariDataSource.setIdleTimeout(10 * 60 * 1000);
        hikariDataSource.setPoolName("Hikari-SQLLite-4-Beans");
        hikariDataSource.setConnectionTestQuery("SELECT 1");
        // 这里是SQLLite数据库，维护一条链接即刻，并非时排队
        hikariDataSource.setMinimumIdle(1);
        hikariDataSource.setMaximumPoolSize(1);
        setDataSource(hikariDataSource);
    }

    @EventListener
    public void listenerApplicationReadyEvent(ApplicationReadyEvent event) {
        log.info("应用已经准备就绪-事件 读取所有bean  ： {} ", DateUtil.now());
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                initDataSource();
                getTableColumns();
                checkTable();
                clearTable();
                readBeans();
            }
        });

    }

    @Override
    public void readBeans() {
        if (ObjectUtil.isNull(beansEndpoint)) {
            log.error(SubscribeMarker.getMarker("BeansEndpoint")
                    , "读取Bean信息失败。BeansEndpoint 未注入。请引入 spring-boot-starter-actuator 模块，并且开启BeansEndpoint ");
            return;
        }
        BeansEndpoint.ApplicationBeans beans = beansEndpoint.beans();
        Map<String, BeansEndpoint.ContextBeans> contexts = beans.getContexts();
        for (String context : contexts.keySet()) {
            BeansEndpoint.ContextBeans contextBeans = contexts.get(context);
            Map<String, BeansEndpoint.BeanDescriptor> beanMap = contextBeans.getBeans();
            if (ObjectUtil.isEmpty(beanMap)) {
                continue;
            }
            for (String beanName : beanMap.keySet()) {
                BeansEndpoint.BeanDescriptor beanDescriptor = beanMap.get(beanName);
                if (ObjectUtil.isNull(beanDescriptor)) {
                    continue;
                }
                AioBeanBo beanBo = new AioBeanBo();
                beanBo.setBeanName(beanName);
                beanBo.setClassName(beanDescriptor.getType().getName());

                String[] aliases = beanDescriptor.getAliases();
                if (ObjectUtil.isNotNull(aliases)) {
                    beanBo.setAliases(JSON.toJSONString(aliases));
                }

                String scope = beanDescriptor.getScope();
                beanBo.setScope(scope);

                try {

                    Class<?> aClass = Class.forName(beanBo.getClassName());
                    Class<?>[] interfaces = aClass.getInterfaces();
                    Class<?> superclass = aClass.getSuperclass();
                    if (ObjectUtil.isNotNull(superclass)) {
                        beanBo.setSuperclass(superclass.getName());
                    }
                    if (ObjectUtil.isEmpty(interfaces)) {
                        beanBo.setInterfaces("[]");
                    } else {
                        List<String> interfaceList = new ArrayList<>();
                        for (Class<?> anInterface : interfaces) {
                            interfaceList.add(anInterface.getName());
                        }
                        beanBo.setInterfaces(JSON.toJSONString(interfaceList));
                    }

                } catch (ClassNotFoundException e) {
                    continue;
                }

                beanBo.setResource(beanDescriptor.getResource());
                String[] dependencies = beanDescriptor.getDependencies();
                if (ObjectUtil.isNotNull(dependencies)) {
                    beanBo.setDependencies(JSON.toJSONString(dependencies));
                }

                addRow(beanBo);
            }
        }
    }
}
