package com.aio.runtime.log.domain;

import lombok.Data;

import java.util.Map;

/**
 * @author lzm
 * @desc 日志对象
 * @date 2024/08/08
 */
@Data
public class AioLogBo {
    private String className;
    private String methodName;
    private String threadName;
    private String level;
    private String id;
    /**
     * 错误码
     */
    private String traceId;
    /**
     * 错误信息
     */
    private String message;
    private Long createTime;
    private String marker;
    private Map<String,String> mdc;
}
