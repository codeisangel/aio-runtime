package com.aio.runtime.log.domain;

import lombok.Data;
import org.springframework.boot.logging.LogLevel;

/**
 * @author lzm
 * @desc 设置日志级别参数
 * @date 2024/08/25
 */
@Data
public class SetLogLevelParams {
    private String name;
    private LogLevel configuredLevel;
    private LogLevel effectiveLevel;
}
