package com.guodun.aio.document;


import com.guodun.aio.document.user.EnableUserModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableCodeGenerate
@EnableUserModel
@EnableAsync
@EnableAspectJAutoProxy
//@EnableSecurityClient4Http
//@EnableZuulProxy
@EnableCaching
@EnableScheduling
public class GuodunDocumentApplication {
    public static void main(String[] args) {

        SpringApplication.run(GuodunDocumentApplication.class, args);

    }

}
