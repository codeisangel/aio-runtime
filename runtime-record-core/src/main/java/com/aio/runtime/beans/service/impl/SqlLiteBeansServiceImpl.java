package com.aio.runtime.beans.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aio.runtime.beans.service.IAioBeansService;
import com.aio.runtime.environment.domain.EnvironmentItemBo;
import com.aio.runtime.environment.domain.QueryEnvironmentParams;
import com.aio.runtime.subscribe.log.SubscribeMarker;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author lzm
 * @desc beans服务SqlLite存储
 * @date 2024/07/31
 */
@Service
@Slf4j
@ConditionalOnClass(BeansEndpoint.class)
public class SqlLiteBeansServiceImpl implements IAioBeansService {
    @Autowired(required = false)
    private BeansEndpoint beansEndpoint;
    @Override
    public PageResult<EnvironmentItemBo> getPage(QueryEnvironmentParams params, KgoPage page) {
        return null;
    }
    @EventListener
    public void listenerApplicationReadyEvent(ApplicationReadyEvent event){
        log.info("应用已经准备就绪-事件 读取所有bean  ： {} ", DateUtil.now());
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                readBeans();
            }
        });

    }
    @Override
    public void readBeans() {
        if (ObjectUtil.isNull(beansEndpoint)){
            log.error(SubscribeMarker.getMarker("BeansEndpoint")
                    , "读取Bean信息失败。BeansEndpoint 未注入。请引入 spring-boot-starter-actuator 模块，并且开启BeansEndpoint ");
            return;
        }
        BeansEndpoint.ApplicationBeans beans = beansEndpoint.beans();
        Map<String, BeansEndpoint.ContextBeans> contexts = beans.getContexts();
        for (String context : contexts.keySet()) {
            BeansEndpoint.ContextBeans contextBeans = contexts.get(context);
            log.info("上下文 [ {} ]  ",context);
            Map<String, BeansEndpoint.BeanDescriptor> beanMap = contextBeans.getBeans();
            if (ObjectUtil.isEmpty(beanMap)){
                continue;
            }
            for (String beanName : beanMap.keySet()) {
                BeansEndpoint.BeanDescriptor beanDescriptor = beanMap.get(beanName);

                log.info("bean名称 [ {} ]  ： {} ",beanName,beanDescriptor);
            }
        }
    }
}
