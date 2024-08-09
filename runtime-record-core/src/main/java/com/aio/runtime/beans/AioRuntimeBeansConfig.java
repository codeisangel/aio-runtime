package com.aio.runtime.beans;

import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzm
 * @desc 订阅配置
 * @date 2024/07/28
 */
@Configuration
@ConditionalOnClass(BeansEndpoint.class)
@ComponentScan({"com.aio.runtime.beans"})
public class AioRuntimeBeansConfig {

}
