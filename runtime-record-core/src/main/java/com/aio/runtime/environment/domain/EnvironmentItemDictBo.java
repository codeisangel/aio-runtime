package com.aio.runtime.environment.domain;

import lombok.Data;

/**
 * @author lzm
 * @desc 环境项字典名称
 * @date 2024/07/28
 */
@Data
public class EnvironmentItemDictBo {
    private String group;
    private String key;
    private String name;
    private String keywords;
}
