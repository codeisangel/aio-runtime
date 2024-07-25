package com.aio.runtime.record.log.subscribe.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author lzm
 * @desc 订阅日志对象 视图层
 * @date 2024/07/24
 */
@Data
public class SubscribeLogVo {
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

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
