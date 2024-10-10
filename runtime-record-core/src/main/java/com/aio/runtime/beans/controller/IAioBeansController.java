package com.aio.runtime.beans.controller;

import cn.aio1024.framework.basic.adapter.user.annotations.AioSecurityVerify;
import cn.aio1024.framework.basic.domain.amis.AmisResult;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.aio.runtime.beans.domain.BeanMethodInfo;
import com.aio.runtime.beans.domain.BeanMethodParameter;
import com.aio.runtime.beans.domain.QueryBeanParams;
import com.aio.runtime.beans.domain.RunMethodParameter;
import com.aio.runtime.beans.service.IAioBeansService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
            Method[] methods = beanClass.getMethods();
            Set<BeanMethodInfo> methodList = new HashSet<>();
            for (Method method : methods){
                BeanMethodInfo methodInfo = new BeanMethodInfo();
                methodInfo.setMethodName(method.getName());
                methodInfo.setParameterTypes(method.getParameterTypes());
                methodInfo.setClassName(params.getClassName());
                methodInfo.setBeanName(params.getBeanName());

                Parameter[] parameters = method.getParameters();
                if (ObjectUtil.isEmpty(parameters)){
                    methodInfo.setMethodDesc(String.format("%s ()",method.getName()));

                }else {
                    StringBuffer paramsDesc = new StringBuffer();
                    for (int i = 0; i < parameters.length; i++) {
                        if (i < (parameters.length-1)){
                            paramsDesc.append(String.format("%s %s ,",parameters[i].getType().getName(),parameters[i].getName()));
                        }else {
                            paramsDesc.append(String.format("%s %s",parameters[i].getType().getName(),parameters[i].getName()));
                        }
                    }
                    methodInfo.setMethodDesc(String.format("%s (%s)",method.getName(),paramsDesc.toString()));
                }
                methodList.add(methodInfo);
            }

            return AmisResult.success(methodList);
        }catch (Exception e){
            log.error("获取Bean所有方法异常。 异常类[ {} ] 异常信息[ {} ]  异常追踪 : {} ",e.getClass(),e.getMessage(),e.getStackTrace());
            return AmisResult.fail("未能获取Bean的方法");
        }
    }
    @PostMapping("method/parameters")
    @AioSecurityVerify
    public AmisResult getBeanMethodParameters(@RequestBody BeanMethodInfo params){
        if (StringUtils.isBlank(params.getClassName())){
            throw new IllegalArgumentException("className不能为空。");
        }
        if (StringUtils.isBlank(params.getMethodName())){
            throw new IllegalArgumentException("方法名不能为空。");
        }
        try {
            Class<?> beanClass = Class.forName(params.getClassName());
            Method method = beanClass.getMethod(params.getMethodName(), params.getParameterTypes());
            Parameter[] parameters = method.getParameters();
            RunMethodParameter result = Convert.convert(RunMethodParameter.class, params);
            if (ObjectUtil.isEmpty(parameters)){
                return AmisResult.success(result);
            }
            for (Parameter parameter : parameters) {
                BeanMethodParameter methodParameter = new BeanMethodParameter();
                methodParameter.setType(parameter.getType().getName());
                methodParameter.setValue(parseParameter(parameter));
                result.addParameter(parameter.getName(),methodParameter);
            }

            return AmisResult.success(result);
        }catch (Exception e){
            log.error("获取Bean所有方法异常。 异常类[ {} ] 异常信息[ {} ]  异常追踪 : {} ",e.getClass(),e.getMessage(),e.getStackTrace());
            return AmisResult.fail("未能获取Bean方法详情的方法");
        }
    }
    private Object basicTypeToDefaultValue(Class<?> type){
        if (type.equals(Integer.class) || type.equals(Long.class) || StringUtils.equalsAny(type.getName(),"int","long")){
            return 0;
        }
        if (type.equals(String.class)){
            return "";
        }
        if (type.equals(Double.class)|| StringUtils.equalsAny(type.getName(),"Double","double")){
            return Double.valueOf("0.0");
        }
        if (type.equals(Float.class) || StringUtils.equalsAny(type.getName(),"Float","float")){
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
        return new HashMap<>();
    }
    private Object parseParameter(Parameter parameter){
        Object defaultValue = basicTypeToDefaultValue(parameter.getType());
        return defaultValue;
    }
    @PostMapping("method/run")
    @AioSecurityVerify
    public AmisResult runBeanMethod(@RequestBody RunMethodParameter params){
        if (StringUtils.isBlank(params.getClassName())){
            throw new IllegalArgumentException("className不能为空。");
        }
        if (StringUtils.isBlank(params.getBeanName())){
            throw new IllegalArgumentException("beanName不能为空。");
        }
        if (StringUtils.isBlank(params.getMethodName())){
            throw new IllegalArgumentException("方法名不能为空。");
        }
        log.info("方法执行参数 ： {} ",JSON.toJSONString(params));
        try {
            Object bean = SpringUtil.getBean(params.getBeanName());
            Class<?> beanClass = bean.getClass();
            Method method = beanClass.getMethod(params.getMethodName(), params.getParameterTypes());
            Parameter[] methodParams = method.getParameters();
            Object[] parameters = new Object[methodParams.length];
            Integer index = 0;
            for (String paramName : params.getParameters().keySet()) {
                Parameter methodParam = methodParams[index];
                BeanMethodParameter methodParameter = params.getParameters().get(paramName);
                Object valueObj = Convert.convert(methodParam.getType(), methodParameter.getValue());
                parameters[index] = valueObj;
                index++;
            }
            log.info("执行bean[ {} ]的[ {} ]方法,参数 : {} ",params.getBeanName(),params.getMethodName(), JSON.toJSONString(parameters));
            Object invoke = method.invoke(bean, parameters);
            log.info("执行bean[ {} ]的[ {} ]方法执行结果 ： {} ",params.getBeanName(),params.getMethodName(),invoke);
            return AmisResult.success(invoke);
        }catch (Exception e){
            log.error("获取Bean所有方法异常。 异常类[ {} ] 异常信息[ {} ]  异常追踪 : {} ",e.getClass(),e.getMessage(),e.getStackTrace());
            return AmisResult.fail("未能获取Bean方法详情的方法");
        }
    }

}
