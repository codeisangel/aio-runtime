package com.aio.runtime.mappings.domain;

import lombok.Data;

/**
 * @author lzm
 * @desc 接口信息实体类
 * @date 2024/07/27
 */
@Data
public class AioMappingBo {
    private String id;
    private String className;
    private String methodName;
    private String url;
    private String httpMethod;
    /**
     * 是否弃用
     */
    private Boolean deprecated;
}
