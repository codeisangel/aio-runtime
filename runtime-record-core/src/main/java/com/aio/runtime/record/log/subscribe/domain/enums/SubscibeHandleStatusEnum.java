package com.aio.runtime.record.log.subscribe.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订阅记录处理状态
 */
@Getter
@AllArgsConstructor
public enum SubscibeHandleStatusEnum {
    HANDLED("handled","已处理"),
    UN_HANDLED("unhandled","未处理");
    private final String status;
    private final String desc;

}
