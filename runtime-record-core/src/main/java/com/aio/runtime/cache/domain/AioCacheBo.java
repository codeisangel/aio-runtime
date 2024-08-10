package com.aio.runtime.cache.domain;

import lombok.Data;

/**
 * @author lzm
 * @desc 缓存信息
 * @date 2024/08/10
 */
@Data
public class AioCacheBo {
    private String cacheManagerName;
    private String cacheName;
    private String target;
}
