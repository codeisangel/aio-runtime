package com.aio.runtime.db.orm.annotations;

import com.aio.runtime.db.orm.domain.enums.AioDbTypeEnum;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AioTable {
     String value() ;
     AioDbTypeEnum type();
     String remark() default "";
}
