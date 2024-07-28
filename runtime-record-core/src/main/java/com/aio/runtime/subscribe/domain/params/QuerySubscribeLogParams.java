package com.aio.runtime.subscribe.domain.params;

import lombok.Data;

/**
 * @author lzm
 * @desc 查询订阅日志参数
 * @date 2024/07/25
 */
@Data
public class QuerySubscribeLogParams {
    private String message;
    private String subscribeName;
    private String className;
    private String methodName;
    private String userId;
    private String companyId;
    private Integer handleStatus;
    private Long createFromTime;
    private Long createToTime;
}
