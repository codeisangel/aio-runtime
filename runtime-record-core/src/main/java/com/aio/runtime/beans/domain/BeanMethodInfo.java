package com.aio.runtime.beans.domain;

import lombok.Data;

/**
 * @author lzm
 * @desc bean方法信息
 * @date 2024/10/09
 */
@Data
public class BeanMethodInfo {
    private String className;
    private String beanName;
    private String methodName;
    private String methodDesc;
    private Class<?>[] parameterTypes;
}
