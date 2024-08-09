package com.aio.runtime.log;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzm
 * @desc 订阅配置
 * @date 2024/07/28
 */
@Configuration
@ConditionalOnProperty(prefix = "aio.runtime.log",name = "enable",havingValue = "true",matchIfMissing = true)
@ComponentScan({"com.aio.runtime.log"})
public class AioLogAppendConfig {

}
