package com.aio.runtime.reported.domain.dao;

import com.aio.runtime.reported.domain.params.AddErrorParams;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author lzm
 * @desc 错误上报数据库对象
 * @date 2024/10/26
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("error_feedback_record")
public class ErrorReportDo extends AddErrorParams {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
