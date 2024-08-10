package com.guodun.aio.document.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.guodun.aio.document.domain.TestCacheConstants;
import com.guodun.security.common.constant.cache.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * @author lzm
 * @desc 缓存配置
 * @date 2024/08/10
 */
@Configuration
@Slf4j
public class CacheTestConfig {

    @Bean(name =  TestCacheConstants.FORM_FIELD_MAP)
    public CacheManager formFieldCaffeineCache(){
        Caffeine caffeine = Caffeine.newBuilder()
                .initialCapacity(50)
                .maximumSize(200)
                .expireAfterWrite(8, TimeUnit.DAYS);
        log.info("【CaffeineCacheManager】  缓存名 ： {} ",TestCacheConstants.FORM_FIELD_MAP);
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setAllowNullValues(true);
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean(name =  CacheConstant.CacheName.USER_SESSION)
    public CacheManager userSessionCacheManage(){
        Caffeine caffeine = Caffeine.newBuilder()
                .initialCapacity(50)
                .maximumSize(200)
                .expireAfterWrite(8, TimeUnit.DAYS);
        log.info("【CaffeineCacheManager】  缓存名 ： {} ",CacheConstant.CacheName.USER_SESSION);
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setAllowNullValues(true);
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
    @Primary
    @Bean(name =  CacheConstant.CacheName.COMPANY_BY_Id)
    public CacheManager company_by_id(){
        Caffeine caffeine = Caffeine.newBuilder()
                .initialCapacity(50)
                .maximumSize(200)
                .expireAfterWrite(8, TimeUnit.DAYS);
        log.info("【CaffeineCacheManager】  缓存名 ： {} ",CacheConstant.CacheName.COMPANY_BY_Id);
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setAllowNullValues(true);
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
