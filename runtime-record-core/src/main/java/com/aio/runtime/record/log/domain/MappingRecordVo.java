package com.aio.runtime.record.log.domain;

import lombok.Data;

import java.util.List;

/**
 * @author lzm
 * @desc 请求日志记录
 * @date 2024/05/13
 */
@Data
public class MappingRecordVo {
    private String id;
    private String createTime;
    private String httpMethod;
    private String url;

    private String userId;
    private String userName;
    private String companyId;
    private String companyName;

    private String mappingClass;
    private String mappingMethod;
    private List<String> paramsClass;
    private String params;
    private String result;
    private String success;
    private String deprecated;
    private String traceId;


    private Object stackTrace;
    private String throwable;
    private String exceptionMsg;


}
