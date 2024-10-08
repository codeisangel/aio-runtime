package com.aio.runtime.subscribe;

import com.aio.runtime.subscribe.domain.properties.AioSubscribeProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzm
 * @desc 订阅配置
 * @date 2024/07/28
 */
@Configuration
@ComponentScan({"com.aio.runtime.subscribe"})
@EnableConfigurationProperties(AioSubscribeProperties.class)
public class AioSubscribeConfig {

}
