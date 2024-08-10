package com.aio.runtime.cache.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.aio.runtime.cache.domain.AioCacheBo;
import com.aio.runtime.subscribe.domain.params.QuerySubscribeLogParams;
import com.kgo.flow.common.domain.amis.AmisResult;
import com.kgo.flow.common.domain.page.KgoPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.cache.CachesEndpoint;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lzm
 * @desc 缓存管理控制器
 * @date 2024/08/10
 */
@RestController
@Slf4j
@RequestMapping("/runtime/aio/cache")
public class AioCacheManageController {
    @Autowired(required = false)
    private CachesEndpoint cachesEndpoint;
    @GetMapping("all")
    public AmisResult getAllCache(@ModelAttribute QuerySubscribeLogParams params , @ModelAttribute KgoPage page){
        CachesEndpoint.CachesReport caches = cachesEndpoint.caches();
        Map<String, CachesEndpoint.CacheManagerDescriptor> cacheManagers = caches.getCacheManagers();

        List<AioCacheBo> cacheBoList = new ArrayList<>();
        for (String cacheManagerName : cacheManagers.keySet()) {
            CachesEndpoint.CacheManagerDescriptor cacheManagerDescriptor = cacheManagers.get(cacheManagerName);
            if (ObjectUtil.isNull(cacheManagerDescriptor)){
                continue;
            }
            Map<String, CachesEndpoint.CacheDescriptor> cachesMap = cacheManagerDescriptor.getCaches();
            if (ObjectUtil.isEmpty(cachesMap)){
                continue;
            }
            cachesMap.forEach((cacheName,v)->{
                String target = v.getTarget();
                 AioCacheBo cacheBo = new AioCacheBo();
                 cacheBo.setCacheManagerName(cacheManagerName);
                 cacheBo.setCacheName(cacheName);
                 cacheBo.setTarget(target);
                 cacheBoList.add(cacheBo);
            });
        }

        return AmisResult.success(cacheBoList);
    }
    @DeleteMapping("clear")
    public AmisResult clearCache(@RequestParam String cacheManagerName,@RequestParam String cacheName){
        CacheManager cacheManager = SpringUtil.getBean(cacheManagerName, CacheManager.class);
        Collection<String> cacheNames = cacheManager.getCacheNames();
        if (!cacheNames.contains(cacheName)){
            return AmisResult.fail(40401, StrUtil.format("缓存管理[ {} ] 下没有缓存名称[ {} ] 缓存。",cacheManagerName,cacheName));
        }
        Cache cache = cacheManager.getCache(cacheName);
        cache.clear();
        return AmisResult.successMsg(StrUtil.format("清空缓存[ {} ]成功",cacheNames));
    }

    @PutMapping("invalidate")
    public AmisResult invalidateCache(@RequestParam String cacheManagerName,@RequestParam String cacheName){
        CacheManager cacheManager = SpringUtil.getBean(cacheManagerName, CacheManager.class);
        Collection<String> cacheNames = cacheManager.getCacheNames();
        if (!cacheNames.contains(cacheName)){
            return AmisResult.fail(40401, StrUtil.format("缓存管理[ {} ] 下没有缓存名称[ {} ] 缓存。",cacheManagerName,cacheName));
        }
        Cache cache = cacheManager.getCache(cacheName);

        boolean invalidate = cache.invalidate();
        return AmisResult.success(invalidate,StrUtil.format("清空缓存[ {} ]成功",cacheNames));
    }

    /**
     * 获取缓存内容
     * @param cacheManagerName 缓存管理名称
     * @param cacheName 缓存名称
     * @param key 缓存KEY
     * @return
     */
    @GetMapping("content")
    public AmisResult getCacheContent(@RequestParam String cacheManagerName,@RequestParam String cacheName,@RequestParam(required = false) String key){
        if (StringUtils.isBlank(key)){
            return AmisResult.fail(40301,StrUtil.format("缓存Key不能为空"));
        }
        CacheManager cacheManager = SpringUtil.getBean(cacheManagerName, CacheManager.class);
        Collection<String> cacheNames = cacheManager.getCacheNames();
        if (!cacheNames.contains(cacheName)){
            return AmisResult.fail(40401, StrUtil.format("缓存管理[ {} ] 下没有缓存名称[ {} ] 缓存。",cacheManagerName,cacheName));
        }
        Cache cache = cacheManager.getCache(cacheName);
        Cache.ValueWrapper valueWrapper = cache.get(key);
        if (ObjectUtil.isNull(valueWrapper)){
            return AmisResult.successMsg(StrUtil.format("缓存 {} 缓存名称 {} 缓存Key下[ {} ] 的值为空",cacheManagerName,cacheName,key));
        }
        return AmisResult.success(valueWrapper.get());
    }
    /**
     * 删除缓存内容
     * @param cacheManagerName 缓存管理名称
     * @param cacheName 缓存名称
     * @param key 缓存KEY
     * @return
     */
    @DeleteMapping("content")
    public AmisResult deleteCacheContent(@RequestParam String cacheManagerName,@RequestParam String cacheName,@RequestParam(required = false) String key){
        if (StringUtils.isBlank(key)){
            return AmisResult.fail(40301,StrUtil.format("缓存Key不能为空"));
        }
        CacheManager cacheManager = SpringUtil.getBean(cacheManagerName, CacheManager.class);

        Collection<String> cacheNames = cacheManager.getCacheNames();
        if (!cacheNames.contains(cacheName)){
            return AmisResult.fail(40401, StrUtil.format("缓存管理[ {} ] 下没有缓存名称[ {} ] 缓存。",cacheManagerName,cacheName));
        }
        Cache cache = cacheManager.getCache(cacheName);
        boolean b = cache.evictIfPresent(key);
        String msg = b ? StrUtil.format("Key[ {} ]删除成功。",key) : StrUtil.format("Key[ {} ] 不存在。",key);
        return AmisResult.success(b,msg);
    }

}
