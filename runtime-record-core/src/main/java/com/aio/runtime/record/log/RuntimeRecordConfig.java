package com.aio.runtime.record.log;

import com.aio.runtime.beans.AioRuntimeBeansConfig;
import com.aio.runtime.cache.AioCacheManageConfig;
import com.aio.runtime.jvm.AioJvmConfig;
import com.aio.runtime.log.AioLogAppendConfig;
import com.aio.runtime.record.log.config.properties.AioMappingLogProperties;
import com.aio.runtime.reported.ErrorReportedConfig;
import com.aio.runtime.security.RuntimeSecurityConfig;
import com.aio.runtime.subscribe.AioSubscribeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * @author lzm
 * @desc 电子印章销售模块配置
 * @date 2024/04/18
 */
@Configuration
@ComponentScan({
        "com.aio.runtime.record.log",
        "com.aio.runtime.environment",
        "com.aio.runtime.common",
        "com.aio.runtime.mappings"
})
@Slf4j
@Import({

        AioSubscribeConfig.class,
        AioRuntimeBeansConfig.class,
        AioRuntimeBeansConfig.class,
        RuntimeSecurityConfig.class,
        AioLogAppendConfig.class,
        AioJvmConfig.class,
        AioCacheManageConfig.class,
        ErrorReportedConfig.class

        })
@EnableConfigurationProperties(AioMappingLogProperties.class)
@PropertySource("classpath:runtime-log-version.properties")
public class RuntimeRecordConfig {

}
