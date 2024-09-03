package com.aio.runtime.beans.controller;

import cn.aio1024.framework.basic.adapter.user.annotations.AioSecurityVerify;
import cn.aio1024.framework.basic.domain.amis.AmisResult;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.aio.runtime.beans.domain.BeanMethodParameter;
import com.aio.runtime.beans.domain.QueryBeanMethodInfoParams;
import com.aio.runtime.beans.domain.QueryBeanParams;
import com.aio.runtime.beans.domain.RunMethodParameter;
import com.aio.runtime.beans.service.IAioBeansService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author lzm
 * @desc bean查询接口
 * @date 2024/08/06
 */
@RestController
@Slf4j
@RequestMapping("/runtime/aio/bean")
public class IAioBeansController {
    @Autowired
    private IAioBeansService beansService;
    @GetMapping("page")
    @AioSecurityVerify
    public AmisResult getEnvironmentItemPage(@ModelAttribute QueryBeanParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = beansService.getPage(params,page);
        return AmisResult.success(pageResult);
    }
    @GetMapping("method/list")
    @AioSecurityVerify
    public AmisResult getBeanMethodList(@ModelAttribute QueryBeanParams params){
        if (StringUtils.isBlank(params.getClassName())){
            throw new IllegalArgumentException("className不能为空。");
        }
        try {
            Class<?> beanClass = Class.forName(params.getClassName());
            Set<String> methodNames = ReflectUtil.getMethodNames(beanClass);
            return AmisResult.success(methodNames);
        }catch (Exception e){
            log.error("获取Bean所有方法异常。 异常类[ {} ] 异常信息[ {} ]  异常追踪 : {} ",e.getClass(),e.getMessage(),e.getStackTrace());
            return AmisResult.fail("未能获取Bean的方法");
        }
    }
    @GetMapping("method/parameters")
    @AioSecurityVerify
    public AmisResult getBeanMethodParameters(@ModelAttribute QueryBeanMethodInfoParams params){
        if (StringUtils.isBlank(params.getClassName())){
            throw new IllegalArgumentException("className不能为空。");
        }
        if (StringUtils.isBlank(params.getMethod())){
            throw new IllegalArgumentException("方法名不能为空。");
        }
        try {
            Class<?> beanClass = Class.forName(params.getClassName());

            Method method = ReflectUtil.getMethodByName(beanClass, params.getMethod());
            Parameter[] parameters = method.getParameters();
            if (ObjectUtil.isEmpty(parameters)){
                return AmisResult.success();
            }
            List<BeanMethodParameter> parameterDescList = new ArrayList<>();
            for (Parameter parameter : parameters) {
                BeanMethodParameter methodParameter = new BeanMethodParameter();
                methodParameter.setName(parameter.getName());
                methodParameter.setType(parameter.getType().getName());
                methodParameter.setValue(parseParameter(parameter));
                parameterDescList.add(methodParameter);
            }
            return AmisResult.success(parameterDescList);
        }catch (Exception e){
            log.error("获取Bean所有方法异常。 异常类[ {} ] 异常信息[ {} ]  异常追踪 : {} ",e.getClass(),e.getMessage(),e.getStackTrace());
            return AmisResult.fail("未能获取Bean方法详情的方法");
        }
    }
    private Object basicTypeToDefaultValue(Class<?> type){
        if (type.equals(Integer.class)){
            return 0;
        }
        if (type.equals(String.class)){
            return " ";
        }
        if (type.equals(Double.class)){
            return Double.valueOf("0.0");
        }
        if (type.equals(Float.class)){
            return Float.valueOf("0.0");
        }
        if (type.equals(BigDecimal.class)){
            return BigDecimal.ONE;
        }
        if (type.equals(Collection.class)){
            return new JSONArray();
        }
        if (type.equals(Map.class)){
            return new HashMap<>();
        }
        return null;
    }
    private Object parseParameter(Parameter parameter){
        Object defaultValue = basicTypeToDefaultValue(parameter.getType());
        if (ObjectUtil.isNotNull(defaultValue)){
            return defaultValue;
        }
        Map<String, Object> paramsMap = new HashMap<>();
        Class<?> type = parameter.getType();
        Field[] fields = type.getFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            Object fieldValue = basicTypeToDefaultValue(fieldType);
            if (ObjectUtil.isNotNull(fieldValue)){
                paramsMap.put(field.getName(), fieldValue);
            } else {
                Map<String, Object> sunParamsMap = new HashMap<>();
                for (Field filedSun : fieldType.getFields()) {
                    Object fieldSunValue = basicTypeToDefaultValue(filedSun.getType());
                    sunParamsMap.put(fieldType.getName(), fieldSunValue);
                }
                paramsMap.put(field.getName(), sunParamsMap);
            }

        }
        return paramsMap;

    }
    @PostMapping("method/run")
    @AioSecurityVerify
    public AmisResult runBeanMethod(@RequestBody RunMethodParameter params){
        if (StringUtils.isBlank(params.getClassName())){
            throw new IllegalArgumentException("className不能为空。");
        }
        if (StringUtils.isBlank(params.getMethod())){
            throw new IllegalArgumentException("方法名不能为空。");
        }
        try {
            Object bean = SpringUtil.getBean(params.getBeanName());
            Class<?> beanClass = bean.getClass();
            Method method = ReflectUtil.getMethodByName(beanClass, params.getMethod());
            Parameter[] methodParams = method.getParameters();
            Object[] parameters = new Object[methodParams.length];
            Integer index = 0;
            for (String paramName : params.getParameters().keySet()) {
                Parameter methodParam = methodParams[index];
                Object paramObj = params.getParameters().get(paramName);
                Object valueObj = Convert.convert(methodParam.getType(), paramObj);
                parameters[index] = valueObj;
                index++;
            }
            log.info("执行bean[ {} ]的[ {} ]方法,参数 : {} ",params.getBeanName(),params.getMethod(), JSON.toJSONString(parameters));
            Object invoke = method.invoke(bean, parameters);
            log.info("执行bean[ {} ]的[ {} ]方法执行结果 ： {} ",params.getBeanName(),params.getMethod(),invoke);
            return AmisResult.success(invoke);
        }catch (Exception e){
            log.error("获取Bean所有方法异常。 异常类[ {} ] 异常信息[ {} ]  异常追踪 : {} ",e.getClass(),e.getMessage(),e.getStackTrace());
            return AmisResult.fail("未能获取Bean方法详情的方法");
        }
    }

}
