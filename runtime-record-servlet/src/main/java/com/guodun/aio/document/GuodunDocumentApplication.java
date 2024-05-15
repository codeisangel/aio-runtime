package com.guodun.aio.document;


import com.guodun.aio.document.user.EnableUserModel;
import com.guodun.security.client.http.EnableSecurityClient4Http;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@EnableCodeGenerate
@EnableUserModel
@EnableAsync
@EnableAspectJAutoProxy
@EnableSecurityClient4Http
@EnableZuulProxy
public class GuodunDocumentApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuodunDocumentApplication.class, args);
    }

}
