package com.aio.runtime.subscribe.domain.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lzm
 * @desc 日志订阅配置
 * @date 2024/10/08
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = AioSubscribeProperties.PREFIX)
public class AioSubscribeProperties {
    public static final String PREFIX = "aio.runtime.log.subscribe";
    private SubscribeFeiShuProperties feishu;
    private String detailAddress;
    private List<String> markers = new ArrayList<>();
}
