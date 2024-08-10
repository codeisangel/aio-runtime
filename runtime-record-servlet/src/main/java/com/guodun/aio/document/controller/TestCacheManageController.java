package com.guodun.aio.document.controller;

import cn.hutool.core.util.IdUtil;
import com.guodun.aio.document.domain.TestCacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

/**
 * @author lzm
 * @desc 测试创建日志
 * @date 2024/08/09
 */
@RestController
@Slf4j
@RequestMapping("/test/cache")
public class TestCacheManageController {
    @Cacheable(cacheNames = TestCacheConstants.FORM_FIELD_MAP,key = "#id",cacheManager = TestCacheConstants.FORM_FIELD_MAP)
    @GetMapping("cache1")
    public String getCache1(@RequestParam String id){
        log.info("查询一个Cache : {} ",id);
        return id;
    }

    @Cacheable(cacheNames = "testCache2",key = "#id",cacheManager = TestCacheConstants.FORM_FIELD_MAP)
    @GetMapping("cache2")
    public String getCache2(@RequestParam String id){
        log.info("查询一个Cache2 : {} ",id);
        return id;
    }

    @Cacheable(cacheNames = "cache_name_3",key = "#id",cacheManager = TestCacheConstants.FORM_FIELD_MAP)
    @GetMapping("cache3")
    public String getCache3(@RequestParam String id){
        log.info("查询一个Cache3 : {} ",id);
        return id;
    }

    @CachePut(cacheNames = TestCacheConstants.FORM_FIELD_MAP,key = "#id",cacheManager = TestCacheConstants.FORM_FIELD_MAP)
    @PutMapping("cache1")
    public String saveCache1(@RequestParam String id){
        log.info("新增一个Cache : {} ",id);
        return id;
    }


    @CacheEvict(value = TestCacheConstants.FORM_FIELD_MAP,cacheManager = TestCacheConstants.FORM_FIELD_MAP,key = "#id")
    @DeleteMapping("cache1")
    public String deleteCache1(@RequestParam String id){
        log.info("这是删除缓存 ： {} ",id);
        return id;
    }
    @PostConstruct
    public void init(){
       getCache1(IdUtil.getSnowflakeNextIdStr());
       getCache2(IdUtil.getSnowflakeNextIdStr());
       getCache3(IdUtil.getSnowflakeNextIdStr());
    }

}
