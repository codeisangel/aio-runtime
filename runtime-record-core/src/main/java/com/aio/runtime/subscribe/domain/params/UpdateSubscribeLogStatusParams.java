package com.aio.runtime.subscribe.domain.params;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author lzm
 * @desc 查询订阅日志参数
 * @date 2024/07/25
 */
@Data
public class UpdateSubscribeLogStatusParams {
    @NotBlank(message = "日志ID不能为空")
    private String id;
}
