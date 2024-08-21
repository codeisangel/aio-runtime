package com.aio.runtime.beans.domain;

import lombok.Data;

/**
 * @author lzm
 * @desc 查询bean接口
 * @date 2024/08/06
 */
@Data
public class QueryBeanMethodInfoParams {
    private String className;
    private String method;

}
