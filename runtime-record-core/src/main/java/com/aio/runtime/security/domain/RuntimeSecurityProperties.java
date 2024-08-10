package com.aio.runtime.security.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lzm
 * @desc 运行时安全配置属性
 * @date 2024/08/10
 */
@Data
@ConfigurationProperties(prefix = "aio.runtime.security")
public class RuntimeSecurityProperties {
    private String username = "admin";
    private String password = "guodun@2024";
    private Boolean enable = false;
}
