package com.aio.runtime.subscribe;

import com.aio.runtime.subscribe.domain.properties.AioSubscribeProperties;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzm
 * @desc 订阅配置
 * @date 2024/07/28
 */
@Slf4j
@Configuration
@MapperScan({"com.aio.runtime.subscribe.mapper"})
@ConditionalOnProperty(prefix = AioSubscribeProperties.PREFIX,name = "scheme",havingValue = "mysql",matchIfMissing = true)
public class AioSubscribeMapperConfig {
    public AioSubscribeMapperConfig() {
        log.info("日志订阅启动mysql数据治理方案");
    }
}
