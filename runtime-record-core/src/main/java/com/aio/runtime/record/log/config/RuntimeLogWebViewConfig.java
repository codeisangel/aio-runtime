package com.aio.runtime.record.log.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lizhenming
 * @desc: web视图配置
 * @date 2022/12/19 14:58
 */

@Configuration
@Slf4j
public class RuntimeLogWebViewConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/view/runtime/**").addResourceLocations("classpath:/templates/runtime/console/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/view/runtime","/view/runtime/index.html");
        registry.addRedirectViewController("/view/runtime/","/view/runtime/index.html");
    }
}

