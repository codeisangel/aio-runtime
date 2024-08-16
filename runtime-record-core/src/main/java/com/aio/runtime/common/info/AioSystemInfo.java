package com.aio.runtime.common.info;

import lombok.Data;

/**
 * @author lzm
 * @desc 系统信息
 * @date 2024/08/16
 */
@Data
public class AioSystemInfo {
    private String maxMemory;
    private String freeMemory;
    private String totalMemory;
}
