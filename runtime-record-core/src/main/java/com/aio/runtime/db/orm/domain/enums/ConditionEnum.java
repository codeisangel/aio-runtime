package com.aio.runtime.db.orm.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConditionEnum {
    EQ("eq","等于"),
    LIKE("like","模糊查询"),
    LIKE_LEFT("likeLeft","模糊查询"),
    NE("ne","不等于");
    private final String condition;
    private final String desc;
}
