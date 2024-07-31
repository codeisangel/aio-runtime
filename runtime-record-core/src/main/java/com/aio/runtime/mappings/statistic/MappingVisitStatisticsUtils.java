package com.aio.runtime.mappings.statistic;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lzm
 * @desc 接口访问统计工具类
 * @date 2024/07/29
 */
public class MappingVisitStatisticsUtils {
    private static final Integer SIZE = 5000;

    // 活跃接口计数器
    private static final Map<String, Integer> LIVELY_MAPPING_COUNTER_STATISTICS_CACHE =  new ConcurrentHashMap(SIZE);
    // 接口活跃时间
    private static final Map<String, Long> LIVELY_MAPPING_TIME_STATISTICS_CACHE =new ConcurrentHashMap(SIZE);
    private static String builderName(String className,String methodName){
       return StrUtil.format("{}##{}",className,methodName);
    }
    public static String getClassName(String classAndMethod){
        if (StringUtils.isBlank(classAndMethod)){
            return null;
        }
        String[] split = StringUtils.split(classAndMethod, "##");
        if (ObjectUtil.isEmpty(split)){
            return null;
        }
        return split[0];
    }
    public static String getMethodName(String classAndMethod){
        if (StringUtils.isBlank(classAndMethod)){
            return null;
        }
        String[] split = StringUtils.split(classAndMethod, "##");
        if (ObjectUtil.isEmpty(split)){
            return null;
        }
        if (split.length<2){
            return null;
        }
        return split[1];
    }

    public static void count(String className,String methodName){
        if (StringUtils.isAnyBlank(className,methodName)){
            return;
        }
        String name = builderName(className,methodName);
        LIVELY_MAPPING_TIME_STATISTICS_CACHE.put(name,System.currentTimeMillis());

        Integer counter = LIVELY_MAPPING_COUNTER_STATISTICS_CACHE.get(name);
        counter = ObjectUtil.isNull(counter) ? 1 : (counter+1);
        LIVELY_MAPPING_COUNTER_STATISTICS_CACHE.put(name,counter);
    }

    public static Integer getCounterAndRemove(String classAndMethod){

        Integer i = LIVELY_MAPPING_COUNTER_STATISTICS_CACHE.get(classAndMethod);
        if (ObjectUtil.isNull(i)){
            return null;
        }
        LIVELY_MAPPING_COUNTER_STATISTICS_CACHE.remove(classAndMethod);
        return i;
    }
    public static Date getLiveLyTimeAndRemove(String classAndMethod){
        Long timeLong = LIVELY_MAPPING_TIME_STATISTICS_CACHE.get(classAndMethod);
        if (ObjectUtil.isNull(timeLong)){
            return null;
        }
        LIVELY_MAPPING_TIME_STATISTICS_CACHE.remove(classAndMethod);
        return new Date(timeLong);
    }
    public static Set<String> getIterator(){
        return LIVELY_MAPPING_COUNTER_STATISTICS_CACHE.keySet();
    }
}
