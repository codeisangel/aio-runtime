package com.guodun.aio.document.user.config;

import com.guodun.aio.document.user.TraceIdInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lzm
 * @desc 服务器Web集成配置
 * @date 2024/02/02
 */
@Slf4j
@Configuration
public class ServletWebInterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private TraceIdInterceptor traceIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceIdInterceptor).addPathPatterns("/**");

    }
}
