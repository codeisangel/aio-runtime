package com.aio.runtime.subscribe.domain.enums;

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

    public boolean eq(Integer statusInt){
       return this.status.equals(statusInt);
    }
    public static String getDesc(Integer status){
        for (SubscibeHandleStatusEnum statusEnum : SubscibeHandleStatusEnum.values()) {
            if (statusEnum.eq(status)){
                return statusEnum.getDesc();
            }
        }
        return null;
    }
}
