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
public class RunMethodParameter extends BeanMethodInfo{

    private Map<String,BeanMethodParameter> parameters = new HashMap<>();
    public void addParameter(String key,BeanMethodParameter parameter){
        parameters.put(key,parameter);
    }
}
