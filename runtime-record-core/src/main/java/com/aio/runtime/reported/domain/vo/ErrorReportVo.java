package com.aio.runtime.reported.domain.vo;

import com.aio.runtime.reported.domain.params.AddErrorParams;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author lzm
 * @desc 错误上报视图层
 * @date 2024/10/26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorReportVo extends AddErrorParams {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
