package com.guodun.aio.document.user;


import com.guodun.aio.document.user.config.UserModelConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({UserModelConfig.class})
public @interface EnableUserModel {
}
