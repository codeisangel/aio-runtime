package com.aio.runtime.beans.domain;

import lombok.Data;

import java.util.List;

/**
 * @author lzm
 * @desc Bean视图对象
 * @date 2024/08/06
 */
@Data
public class AioBeanVo {
    private String id;
    private String beanName;
    private String className;
    private List<String> dependencies;
    private String scope;
    private List<String> aliases;
    private List<String> interfaces;
    private String superclass;
    private String resource;
}
