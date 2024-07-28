package com.aio.runtime.environment.domain;

import lombok.Data;

/**
 * @author lzm
 * @desc 查询环境条件
 * @date 2024/07/26
 */
@Data
public class QueryEnvironmentParams {
    private String environmentGroup;
    private String propertyKey;
    private String propertyValue;
    private String propertyDesc;
    private String propertyType;
}
