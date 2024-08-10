package com.aio.runtime.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.cache.CachesEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzm
 * @desc 缓存统一配置
 * @date 2024/08/10
 */
@Configuration
@ConditionalOnClass(CachesEndpoint.class)
@Slf4j
@ComponentScan({"com.aio.runtime.cache"})
public class AioCacheManageConfig {

}
