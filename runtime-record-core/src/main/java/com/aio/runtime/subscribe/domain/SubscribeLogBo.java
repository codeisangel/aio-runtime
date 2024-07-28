package com.aio.runtime.subscribe.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author lzm
 * @desc 订阅日志对象
 * @date 2024/07/24
 */
@Data
public class SubscribeLogBo {
    /**
     *
     * 用户ID
     * 企业ID
     *
     *
     *
     * 发生时间
     *
     *
     *
     *
     */
    private String id;
    private String userId;
    private String subscribeName;
    private String companyId;
    /**
     * 错误码
     */
    private String traceId;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 错误信息
     */
    private String message;
    private Integer handleStatus;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 处理时间
     */
    private Date handleTime;
}
