package com.aio.runtime.record.log.subscribe.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订阅记录处理状态
 */
@Getter
@AllArgsConstructor
public enum SubscibeHandleStatusEnum {
    HANDLED(1,"已处理"),
    UN_HANDLED(0,"未处理");
    private final Integer status;
    private final String desc;

}
