package com.aio.runtime.security;

import com.aio.runtime.security.domain.RuntimeSecurityProperties;
import com.kgo.framework.basic.adapter.user.AioSecurityAdapter;
import com.kgo.framework.basic.adapter.user.impl.AioSecurityAdapter4RAM;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(RuntimeSecurityProperties.class)
@ConditionalOnProperty(prefix = "aio.runtime.security",name = "enable",havingValue = "true")
public class RuntimeSecurityConfig {

    @Bean
    public AioSecurityAdapter aioSecurityAdapter4RAM(){
        return new AioSecurityAdapter4RAM();
    }
}
