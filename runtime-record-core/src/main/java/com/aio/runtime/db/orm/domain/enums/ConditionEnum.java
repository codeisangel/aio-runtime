package com.aio.runtime.db.orm.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConditionEnum {
    EQ("eq","等于"),
    LIKE("like","模糊查询"),
    BETWEEN("between","在两者之间"),
    LIKE_LEFT("likeLeft","模糊查询"),
    GT("gt","大于"),
    LT("lt","小于"),
    NE("ne","不等于");
    private final String condition;
    private final String desc;
}
