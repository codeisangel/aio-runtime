package com.aio.runtime.environment.domain;

import lombok.Data;

/**
 * @author lzm
 * @desc 环境信息
 * @date 2024/07/26
 */
@Data
public class EnvironmentItemBo {
    private String id;
    private String environmentGroup;
    private String propertyKey;
    private String propertyValue;
    private String propertyDesc;
    private String propertyType;

}
