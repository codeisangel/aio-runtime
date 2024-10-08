package com.aio.runtime.subscribe.domain.properties;

import lombok.Data;

/**
 * @author lzm
 * @desc 订阅飞书属性
 * @date 2024/10/08
 */
@Data
public class SubscribeFeiShuProperties {
    private String webhook;
    private String secret;
}
