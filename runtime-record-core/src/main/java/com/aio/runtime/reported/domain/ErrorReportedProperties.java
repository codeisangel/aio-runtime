package com.aio.runtime.reported.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lzm
 * @desc 错误上报配置
 * @since 1.0.0
 * @date 2024/08/10
 */
@Data
@ConfigurationProperties(prefix = ErrorReportedProperties.PREFIX)
public class ErrorReportedProperties {
    public static final String PREFIX = "aio.runtime.error.reported";
    private Boolean enable = true;
    private String scheme;
}
