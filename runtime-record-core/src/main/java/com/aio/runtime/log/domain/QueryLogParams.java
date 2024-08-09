package com.aio.runtime.log.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lzm
 * @desc 查询日志参数
 * @date 2024/08/08
 */
@Data
public class QueryLogParams {
    private String className;
    private String methodName;
    private String threadName;
    private String level;
    /**
     * 错误码
     */
    private String traceId;
    /**
     * 错误信息
     */
    private String message;
    private Long createToTime;
    private Long createFromTime;
    private List<String> keywords;
    private String marker;
    private Map<String,String> mdc;
}
