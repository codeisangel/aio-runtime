package com.aio.runtime.subscribe.domain.bo;

import lombok.Data;

import java.util.Date;

/**
 * @author lzm
 * @desc 错误码维护
 * @date 2025/04/08
 */
@Data
public class ErrorCodeBo {
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
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
