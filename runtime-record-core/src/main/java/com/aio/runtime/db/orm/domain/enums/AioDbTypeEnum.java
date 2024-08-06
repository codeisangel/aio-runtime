package com.aio.runtime.db.orm.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AioDbTypeEnum {
    MYSQL("mysql","mysql"),
    SQL_LITE("sqlLite","sqlLite");
    private final String type;
    private final String desc;
}
