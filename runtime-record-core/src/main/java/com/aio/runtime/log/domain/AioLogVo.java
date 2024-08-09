package com.aio.runtime.log.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @author lzm
 * @desc 日志对象
 * @date 2024/08/08
 */
@Data
public class AioLogVo {
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Long createTimestamp;
    private String marker;
    private Map<String,String> mdc;
}
