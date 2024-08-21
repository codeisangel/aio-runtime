package com.aio.runtime.beans.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lzm
 * @desc Bean方法参数
 * @date 2024/08/20
 */
@Data
public class RunMethodParameter {
    private String beanName;
    private String className;
    private String method;
    private Map<String,Object> parameters = new HashMap<>();

}
