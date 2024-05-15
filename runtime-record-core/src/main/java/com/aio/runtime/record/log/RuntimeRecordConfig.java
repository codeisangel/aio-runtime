package com.aio.runtime.record.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author lzm
 * @desc 电子印章销售模块配置
 * @date 2024/04/18
 */
@Configuration
@ComponentScan
@Slf4j
@PropertySource("classpath:runtime-log-version.properties")
public class RuntimeRecordConfig {

}
