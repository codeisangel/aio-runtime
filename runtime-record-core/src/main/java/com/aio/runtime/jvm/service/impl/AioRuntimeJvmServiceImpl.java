package com.aio.runtime.jvm.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.aio.runtime.db.orm.service.AbstractAioSimpleMapper;
import com.aio.runtime.jvm.domain.AioJvmBo;
import com.aio.runtime.jvm.domain.AioJvmCollectBo;
import com.aio.runtime.jvm.service.AioRuntimeJvmService;
import com.alibaba.fastjson.JSON;
import com.kgo.flow.common.domain.constants.ProjectWorkSpaceConstants;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author lzm
 * @desc 运行时JVM数据服务
 * @date 2024/08/16
 */
@Service
@Slf4j
@ConditionalOnClass(MetricsEndpoint.class)
public class AioRuntimeJvmServiceImpl extends AbstractAioSimpleMapper<AioJvmBo> implements AioRuntimeJvmService {
    @Value(ProjectWorkSpaceConstants.CONFIG_PATH_SPEL)
    private String projectWorkspace;
    @Autowired(required = false)
    private MetricsEndpoint metricsEndpoint;
    private ScheduledExecutorService executorJvmStatisticsTaskService = Executors.newScheduledThreadPool(1);
    private void initDataSource() {
        if (!FileUtil.exist(projectWorkspace)) {
            FileUtil.mkdir(projectWorkspace);
        }
        String sqlLitePath = StrUtil.format("jdbc:sqlite:{}/aio_jvm_statistics.db", projectWorkspace);
        log.info("JVM数据统计 sqlLite数据源。 sqlLite数据库地址 ： {} ", sqlLitePath);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setAutoCommit(true);
        hikariDataSource.setJdbcUrl(sqlLitePath);
        // 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
        hikariDataSource.setConnectionTimeout(10 * 60 * 1000);
        // 一个连接空闲状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
        hikariDataSource.setIdleTimeout(10 * 60 * 1000);
        hikariDataSource.setPoolName("Hikari-SQLLite-4-jvm-statistics");
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
            }
        });
    }


    public AioRuntimeJvmServiceImpl(){
        Runnable task = () -> {
            jvmStatistics();
        };
        executorJvmStatisticsTaskService.scheduleAtFixedRate(task, 1, 10, TimeUnit.MINUTES);
        //executorJvmStatisticsTaskService.scheduleAtFixedRate(task, 5, 10, TimeUnit.SECONDS);
    }

    private void jvmStatistics(){
        AioJvmCollectBo jvmCollectBo = new AioJvmCollectBo();
        MetricsEndpoint.ListNamesResponse listNamesResponse = metricsEndpoint.listNames();
        for (String name : listNamesResponse.getNames()) {
            MetricsEndpoint.MetricResponse metric = metricsEndpoint.metric(name, null);
            jvmCollectBo.collect(metric);
        }
        jvmCollectBo.setCreateTime(new Date());
        addRow(jvmCollectBo);
        log.info("收集的数据 ： {} ", JSON.toJSONString(jvmCollectBo));
    }


}
