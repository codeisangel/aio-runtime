package com.aio.runtime.record.log.config.properties;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lzm
 * @desc 日志属性配置
 * @date 2024/08/10
 */
@Data
@ConfigurationProperties(prefix = "aio.runtime.mapping.log")
public class AioMappingLogProperties {
    private Boolean enable = true;
    /**
     * 请求地址黑名单
     */
    private List<String> blackList = new ArrayList<>();

    public void addBlackUrl(String url){
        if (StringUtils.isBlank(url)){
            return;
        }
        blackList.add(url);
    }
}
