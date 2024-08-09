package com.aio.runtime.security;

import com.kgo.framework.basic.adapter.user.AioSecurityAdapter;
import com.kgo.framework.basic.adapter.user.impl.AioSecurityAdapter4RAM;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzm
 * @desc 安全模块配置
 * @date 2024/08/07
 */
@Configuration
@ComponentScan({"com.aio.runtime.security"})
public class RuntimeSecurityConfig {

    @Bean
    public AioSecurityAdapter aioSecurityAdapter4RAM(){
        return new AioSecurityAdapter4RAM();
    }
}
