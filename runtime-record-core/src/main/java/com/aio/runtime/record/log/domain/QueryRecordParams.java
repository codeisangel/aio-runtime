package com.aio.runtime.record.log.domain;

import lombok.Data;

/**
 * @author lzm
 * @desc 查询记录参数
 * @date 2024/05/13
 */
@Data
public class QueryRecordParams {
    private Long createFromTime;
    private Long createToTime;
    private String userName;
    private String httpMethod;
    private String url;
    private String userId;
    private String companyId;
    private String companyName;


    private String mappingClass;
    private String mappingMethod;
    private String params;
    private String result;
    private String traceId;
    private String stackTrace;
    private String throwable;
    private String exceptionMsg;
    private String success;
    private String deprecated;

}
