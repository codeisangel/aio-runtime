package com.aio.runtime.db.orm.conditions;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import com.aio.runtime.db.orm.domain.bo.Condition;
import com.aio.runtime.db.orm.domain.enums.ConditionEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author lzm
 * @desc 查询条件
 * @date 2024/08/06
 */
@Slf4j
@Getter
public class AioQueryCondition<T> {
    private Class<T> doClass;
    public AioQueryCondition() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            Type[] types = parameterizedType.getActualTypeArguments();
            this.doClass = (Class<T>) types[0];
        }
    }
    private List<Condition> conditionList = new ArrayList<>();
    public AioQueryCondition eq(Func1<T,String> fieldFunc,Object value){
        Condition condition = new Condition();
        condition.setCondition(ConditionEnum.EQ);
        condition.setFieldName(LambdaUtil.getFieldName(fieldFunc));
        condition.setValue(value);
        conditionList.add(condition);
        return this;
    }
    public AioQueryCondition ne(Func1<T,String> fieldFunc,Object value){
        Condition condition = new Condition();
        condition.setCondition(ConditionEnum.NE);
        condition.setFieldName(LambdaUtil.getFieldName(fieldFunc));
        condition.setValue(value);
        conditionList.add(condition);
        return this;
    }
    public AioQueryCondition gt(Func1<T,?> fieldFunc,Object value){
        Condition condition = new Condition();
        condition.setCondition(ConditionEnum.GT);
        condition.setFieldName(LambdaUtil.getFieldName(fieldFunc));
        condition.setValue(value);
        conditionList.add(condition);
        return this;
    }

    public AioQueryCondition lt(Func1<T,?> fieldFunc,Object value){
        Condition condition = new Condition();
        condition.setCondition(ConditionEnum.LT);
        condition.setFieldName(LambdaUtil.getFieldName(fieldFunc));
        condition.setValue(value);
        conditionList.add(condition);
        return this;
    }

    public AioQueryCondition between(Func1<T,?> fieldFunc,Object start,Object end){
        Condition condition = new Condition();
        condition.setCondition(ConditionEnum.BETWEEN);
        condition.setFieldName(LambdaUtil.getFieldName(fieldFunc));
        List<Object> betweenList = new ArrayList<>();
        betweenList.add(start);
        betweenList.add(end);
        condition.setValue(betweenList);
        conditionList.add(condition);
        return this;
    }
    public <S> AioQueryCondition between(Func1<T,S> fieldFunc, Collection<S> between){
        Condition condition = new Condition();
        condition.setCondition(ConditionEnum.BETWEEN);
        condition.setFieldName(LambdaUtil.getFieldName(fieldFunc));
        condition.setValue(between);
        conditionList.add(condition);
        return this;
    }
    public AioQueryCondition between(Func1<T,?> fieldFunc,Object... between){
        Condition condition = new Condition();
        condition.setCondition(ConditionEnum.BETWEEN);
        condition.setFieldName(LambdaUtil.getFieldName(fieldFunc));
        condition.setValue(Arrays.asList(between));
        conditionList.add(condition);
        return this;
    }
    public AioQueryCondition like(Func1<T,String> fieldFunc,Object value){
        Condition condition = new Condition();
        condition.setCondition(ConditionEnum.LIKE);
        condition.setFieldName(LambdaUtil.getFieldName(fieldFunc));
        condition.setValue(value);
        conditionList.add(condition);
        return this;
    }
    public AioQueryCondition likeLeft(Func1<T,String> fieldFunc,Object value){
        Condition condition = new Condition();
        condition.setCondition(ConditionEnum.LIKE_LEFT);
        condition.setFieldName(LambdaUtil.getFieldName(fieldFunc));
        condition.setValue(value);
        conditionList.add(condition);
        return this;
    }



}
