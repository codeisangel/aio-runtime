package com.aio.runtime.beans.domain;

import lombok.Data;

/**
 * @author lzm
 * @desc 查询bean接口
 * @date 2024/08/06
 */
@Data
public class QueryBeanParams {
    private String beanName;
    private String className;
    private String dependencies;
    private String scope;
    private String aliases;
    private String interfaceName;
    private String superclass;
}
