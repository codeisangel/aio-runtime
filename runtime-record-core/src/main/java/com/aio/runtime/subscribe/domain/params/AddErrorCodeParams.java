package com.aio.runtime.subscribe.domain.params;

import lombok.Data;

/**
 * @author lzm
 * @desc 查询订阅日志参数
 * @date 2024/07/25
 */
@Data
public class AddErrorCodeParams {
    /**
     * 错误码
     */
    private String id;
    /**
     * 错误码内容
     */
    private String content;
    /**
     * 所属模块
     */
    private String module;
    /**
     * 解决方案
     */
    private String solution;
}
