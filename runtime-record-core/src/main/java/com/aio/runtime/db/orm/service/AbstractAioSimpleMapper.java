package com.aio.runtime.db.orm.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import com.aio.runtime.db.orm.annotations.AioField;
import com.aio.runtime.db.orm.annotations.AioId;
import com.aio.runtime.db.orm.annotations.AioTable;
import com.aio.runtime.db.orm.conditions.AioQueryCondition;
import com.aio.runtime.db.orm.domain.bo.Condition;
import com.aio.runtime.db.orm.domain.enums.ConditionEnum;
import com.aio.runtime.db.orm.utils.CamelToSnakeUtils;
import com.alibaba.fastjson.JSON;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public abstract class AbstractAioSimpleMapper<T> {
    private Class<T> ormObject;
    private DataSource ds;
    private Map<String,Field> fieldMap = new HashMap<>();
    private String tableName;

    public AbstractAioSimpleMapper() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            Type[] types = parameterizedType.getActualTypeArguments();
            this.ormObject = (Class<T>) types[0];
            AioTable aioTableAnnotation = ormObject.getAnnotation(AioTable.class);
            if (ObjectUtil.isNull(aioTableAnnotation)) {
                log.error("创建ORM对象时，获取TABLE注解失败");
                return;
            }
            tableName = aioTableAnnotation.value();
            if (StringUtils.isBlank(tableName)) {
                tableName = CamelToSnakeUtils.convertCamelToSnake(ormObject.getSimpleName());
            }

            Field[] fields = ormObject.getDeclaredFields();

            if (ObjectUtil.isEmpty(fields)) {
                log.error("创建ORM对象时，对象字段为空");
                return;
            }
            for (Field field : fields) {
                fieldMap.put(field.getName(),field);
            }
        }
    }
    public T getById(String id){
        Entity where = Entity.create(tableName).set("id", id);
        try {
            List<Entity> all = Db.use(ds).findAll(where);
            if (ObjectUtil.isNull(all)){
                return null;
            }
            return all.get(0).toBean(ormObject);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateById(T obj){
        Object fieldValue = ReflectUtil.getFieldValue(obj, "id");
        Entity where = Entity.create(tableName).set("id", fieldValue);
        Entity entity = convert(obj);
        try {
            Db.use(ds).update(
                    entity,
                    where
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addRow(T obj){
        Entity entity = convert(obj);
        try {
            int insert = Db.use(ds).insert(entity);
            log.debug("新增数据结果 : {} ",insert);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private Entity convert(T obj){
        Entity entity = Entity.create(tableName);
        for (String fieldName : fieldMap.keySet()) {
            Field field = fieldMap.get(fieldName);
            String columnName = getColumnName(field);
            Object fieldValue = ReflectUtil.getFieldValue(obj, field);
            AioId aioIdAnnotation = field.getAnnotation(AioId.class);
            if (ObjectUtil.isNotNull(aioIdAnnotation)){
                fieldValue = ObjectUtil.isNull(fieldValue) ? IdUtil.getSnowflakeNextIdStr() : fieldValue;
                if (StringUtils.isBlank(columnName)){
                    columnName = aioIdAnnotation.value();
                }
            }
            entity.set(columnName,fieldValue);
        }
        return entity;
    }
    public long addRows(List<T> objs){
        if (ObjectUtil.isEmpty(objs)){
            return 0;
        }
        List<Entity> entities = new ArrayList<>();
        for (T obj : objs) {
            entities.add(convert(obj));
        }
        try {
            int[] insert = Db.use(ds).insert(entities);
            log.debug("新增数据结果 : {} ",insert.length);
            return insert.length;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public PageResult<T> getPage(AioQueryCondition<T> conditions, KgoPage page){
        Entity where = Entity.create(tableName);
        for (Condition condition : conditions.getConditionList()) {
            ConditionEnum conditionEnum = condition.getCondition();
            if (ObjectUtil.isNull(conditionEnum)){
                continue;
            }
            Field field = fieldMap.get(condition.getFieldName());
            if (ObjectUtil.isNull(field)){
                continue;
            }
            if (ObjectUtil.isNull(condition.getValue())){
                continue;
            }
            buildCondition(where,conditionEnum,field,condition.getValue());
        }

        Page hutoolPage = new Page(page.getPageNum() - 1, page.getPageSize());
        try {
            cn.hutool.db.PageResult<Entity> hutoolPageResult = Db.use(ds).page(where, hutoolPage);
            List<T> resultList = new ArrayList<>();
            for (Entity entity : hutoolPageResult) {
                T bean = entity.toBean(ormObject);
                resultList.add(bean);
            }
            PageResult<T> pageResult = new PageResult<>(resultList,hutoolPageResult.getTotal());
            return pageResult;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void buildCondition(Entity where,ConditionEnum conditionEnum,Field field,Object value){
        String columnName = getColumnName(field);
        if (StringUtils.isBlank(columnName)){
            return;
        }
        if (ConditionEnum.EQ.equals(conditionEnum)){
            where.set(columnName,value);
        }else if (ConditionEnum.NE.equals(conditionEnum)){
            where.set(columnName,String.format("<> %s",value));
        }else if (ConditionEnum.LIKE.equals(conditionEnum)){
            where.set(columnName, StrUtil.format("like %{}%",value));
        }else {
            where.set(columnName,value);
        }
    }
    private String getColumnName(Field field){
        AioField aioFieldAnnotation = field.getAnnotation(AioField.class);
        if (ObjectUtil.isNull(aioFieldAnnotation)) {
            return null;
        }
        String fieldName = aioFieldAnnotation.value();
        if (StringUtils.isBlank(fieldName)) {
            fieldName = CamelToSnakeUtils.convertCamelToSnake(field.getName());
        }
        return fieldName;
    }

    public void checkTable() {
        if (existTable()) {

        } else {
            createTable();
        }
    }
    public void clearTable(){
        try {
            Db.use(ds).execute("DELETE FROM  "+tableName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Object> getTableColumns(){
        try {
            List<Entity> entityList = Db.use(ds).query(String.format("PRAGMA table_info(%s);",tableName) );
            log.info("表的所有列 ： {} ", JSON.toJSONString(entityList));
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    private boolean existTable() {
        try {
            Entity entity = Db.use(ds).queryOne("SELECT * FROM sqlite_master WHERE type='table' AND name= ? ", tableName);
            if (ObjectUtil.isNull(entity)) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private void createTable() {


        StringBuffer sql = new StringBuffer();
        sql.append(String.format("CREATE TABLE '%s' (", tableName));
        sql.append(" 'id' text NOT NULL,");
        for (Field field : fieldMap.values()) {
            if (ObjectUtil.isNull(field)) {
                continue;
            }
            AioField aioFieldAnnotation = field.getAnnotation(AioField.class);
            if (ObjectUtil.isNull(aioFieldAnnotation)) {
                continue;
            }
            String fieldName = aioFieldAnnotation.value();
            if (StringUtils.isBlank(fieldName)) {
                fieldName = CamelToSnakeUtils.convertCamelToSnake(field.getName());
            }
            String fieldType = aioFieldAnnotation.type();
            if (StringUtils.isBlank(fieldType)) {
                if (field.getType().equals(String.class)) {
                    fieldType = "text";
                } else if (field.getType().equals(Integer.class)) {
                    fieldType = "integer";
                } else if (field.getType().equals(Long.class)) {
                    fieldType = "integer";
                } else if (field.getType().equals(Date.class)) {
                    fieldType = "integer";
                } else {
                    fieldType = "text";
                }
            }
            sql.append(String.format(" '%s' %s,", fieldName, fieldType));
        }
        sql.append("  PRIMARY KEY (\"id\") )");
        try {
            Db.use(ds).execute(sql.toString());
        } catch (SQLException e) {
            log.error("[创建BEAN存储SQLLite数据库失败] 异常信息[ {} ] 异常类[ {} ]",e.getMessage(),e.getClass());
            throw new RuntimeException(e);
        }
    }

    public DataSource getDataSource() {
        return ds;
    }

    public void setDataSource(DataSource ds) {
        this.ds = ds;
    }
}
