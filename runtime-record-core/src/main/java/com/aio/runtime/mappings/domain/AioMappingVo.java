package com.aio.runtime.mappings.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author lzm
 * @desc 接口信息实体类
 * @date 2024/07/27
 */
@Data
public class AioMappingVo {
    private String id;
    private String className;
    private String methodName;
    private String url;
    private String httpMethod;
    /**
     * 是否弃用
     */
    private Boolean deprecated;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date activeTime;
    private Long visitCounter;
}
