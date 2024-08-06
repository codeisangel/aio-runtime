package com.aio.runtime.db.orm.domain.bo;

import com.aio.runtime.db.orm.domain.enums.ConditionEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lzm
 * @desc 条件
 * @date 2024/08/06
 */
@Data
@Accessors(chain = true)
public class Condition {
    private String fieldName;
    private ConditionEnum condition;
    private Object value;
}
