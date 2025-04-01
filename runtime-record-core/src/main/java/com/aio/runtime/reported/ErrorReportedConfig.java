package com.aio.runtime.reported;

import com.aio.runtime.reported.domain.ErrorReportedProperties;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzm
 * @desc 错误上报
 * @date 2024/10/17
 */
@ComponentScan("com.aio.runtime.reported")
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = ErrorReportedProperties.PREFIX, value = "enable", havingValue = "true", matchIfMissing = true)
@MapperScan("com.aio.runtime.reported.mapper")
@EnableConfigurationProperties(ErrorReportedProperties.class)
public class ErrorReportedConfig {
    private final ErrorReportedProperties errorReportedProperties;
    public ErrorReportedConfig(ErrorReportedProperties properties) {
        this.errorReportedProperties = properties;
        log.info("错误上报初始化，配置属性 ： {}  ", JSON.toJSONString(errorReportedProperties));
    }
}
