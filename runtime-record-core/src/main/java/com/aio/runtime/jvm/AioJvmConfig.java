package com.aio.runtime.jvm;

import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzm
 * @desc jvm配置
 * @date 2024/08/16
 */
@Configuration
@ComponentScan({"com.aio.runtime.jvm"})
@ConditionalOnClass(MetricsEndpoint.class)
public class AioJvmConfig {
}
