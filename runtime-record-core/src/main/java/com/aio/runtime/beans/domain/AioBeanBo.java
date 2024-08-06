package com.aio.runtime.beans.domain;

import com.aio.runtime.db.orm.annotations.AioField;
import com.aio.runtime.db.orm.annotations.AioId;
import com.aio.runtime.db.orm.annotations.AioTable;
import com.aio.runtime.db.orm.domain.enums.AioDbTypeEnum;
import lombok.Data;

/**
 * @author lzm
 * @desc Bean信息
 * @date 2024/07/31
 */
@Data
@AioTable(value = "aio_bean",type = AioDbTypeEnum.SQL_LITE)
public class AioBeanBo {
    @AioId()
    private String id;
    @AioField("bean_name")
    private String beanName;
    @AioField("class_name")
    private String className;
    @AioField("dependencies")
    private String dependencies;
    @AioField("scope")
    private String scope;
    @AioField("aliases")
    private String aliases;
    @AioField("interfaces")
    private String interfaces;
    @AioField("superclass")
    private String superclass;
    @AioField("resource")
    private String resource;
}
