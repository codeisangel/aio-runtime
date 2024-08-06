package com.aio.runtime.db.orm.annotations;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AioId {
     String value() default "id";
     String type() default "";
     String remark() default "";
}
