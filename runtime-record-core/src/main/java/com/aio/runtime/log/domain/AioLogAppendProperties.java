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
    /**
     * 日志保存时间 单位天
     */
    private Integer pastDay = 90;
    /**
     * 索引周期 默认小时 hour , 可选 day
     */
    private String indexPeriod = "hour";
}
