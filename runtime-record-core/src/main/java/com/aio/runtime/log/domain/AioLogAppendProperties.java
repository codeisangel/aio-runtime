package com.aio.runtime.log.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lzm
 * @desc 日志属性配置
 * @date 2024/08/10
 */
@Data
@ConfigurationProperties(prefix = "aio.runtime.log")
public class AioLogAppendProperties {
    private Boolean enable = true;
    private Integer pastday = 90;
}
