package com.aio.runtime.log.domain;

import com.aio.runtime.db.orm.annotations.AioField;
import com.aio.runtime.db.orm.annotations.AioId;
import com.aio.runtime.db.orm.annotations.AioTable;
import com.aio.runtime.db.orm.domain.enums.AioDbTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * @author lzm
 * @desc 日志参数
 * @date 2024/08/09
 */
@Data
@AioTable(value = "aio_log_scheme",type = AioDbTypeEnum.SQL_LITE)
public class AioLogParamsBo {
    @AioId()
    private String id;
    @AioField("scheme_name")
    private String schemeName;
    @AioField("scheme_content")
    private String schemeContent;
    @AioField("update_time")
    private Date updateTime;

}
