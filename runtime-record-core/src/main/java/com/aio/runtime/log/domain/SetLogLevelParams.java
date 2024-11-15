package com.aio.runtime.log.domain;

import lombok.Data;

/**
 * @author lzm
 * @desc 设置日志级别参数
 * @date 2024/08/25
 */
@Data
public class SetLogLevelParams {
    private String name;
    private String configuredLevel;
    private String effectiveLevel;
}
