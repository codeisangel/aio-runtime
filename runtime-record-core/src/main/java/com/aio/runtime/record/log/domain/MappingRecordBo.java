package com.aio.runtime.record.log.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author lzm
 * @desc 请求日志记录
 * @date 2024/05/13
 */
@Data
public class MappingRecordBo {
    private String id;
    private long createTimestamp;
    private Date createTime;
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
    private String traceId;
    private String stackTrace;
    private String throwable;
    private String exceptionMsg;
    private Boolean success;
    private Boolean deprecated = false;


    public void setBasicUrl(String url) {
        if (!StringUtils.startsWith(url, "/")) {
            this.url = "/" + url;
        } else {
            this.url = url;
        }

    }

    public void appendUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        if (StringUtils.isBlank(this.url)) {
            setBasicUrl(url);
            return;
        }

        boolean prefix = StringUtils.startsWith(url, "/");
        boolean suffix = StringUtils.endsWith(this.url, "/");
        if ((!suffix) && (!prefix)) {
            this.url = this.url + "/" + url;
            return;
        }

        this.url = this.url + url;


    }

}
