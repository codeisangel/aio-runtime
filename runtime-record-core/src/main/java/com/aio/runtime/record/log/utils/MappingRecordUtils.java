package com.aio.runtime.record.log.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.aio.runtime.record.log.domain.MappingRecordBo;
import com.alibaba.fastjson.JSON;
import com.kgo.framework.basic.domain.trace.TraceId;
import com.kgo.framework.basic.integration.user.AioUserApi;
import com.kgo.framework.basic.integration.user.domain.AioUser;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lzm
 * @desc 请求记录工具
 * @date 2024/05/13
 */

public class MappingRecordUtils {
    public static MappingRecordBo builder(JoinPoint joinPoint){
        if (ObjectUtil.isEmpty(joinPoint)){
            return null;
        }

        MappingRecordBo mappingRecordBo = new MappingRecordBo();
        mappingRecordBo.setId(IdUtil.getSnowflakeNextIdStr());
        mappingRecordBo.setCreateTimestamp(System.currentTimeMillis());
        appendTokenInfo(mappingRecordBo);

        handleMethodSignature(mappingRecordBo,joinPoint);

        handleTraceId(mappingRecordBo);

        return mappingRecordBo;
    }
    private static void handleTraceId(MappingRecordBo mappingRecordBo){
        if (StringUtils.isBlank(TraceId.getTraceId())){
            String traceId = MDC.get("traceId");
            if (StringUtils.isNotBlank(traceId)){
                TraceId.setTraceId(traceId);
            }
        }
        mappingRecordBo.setTraceId(TraceId.getTraceId());
    }
    private static void handleMethodSignature(MappingRecordBo mappingRecordBo,JoinPoint joinPoint){
        if (ObjectUtil.isEmpty(mappingRecordBo)){
            return;
        }
        Signature signature = joinPoint.getSignature();
        if (ObjectUtil.isEmpty(signature)){
            return;
        }
        Object target = joinPoint.getTarget();
        mappingRecordBo.setMappingClass(target.getClass().getName());


        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (ObjectUtil.isEmpty(method)){
            return;
        }

        Class clazz = methodSignature.getDeclaringType();
        Annotation annotation = clazz.getAnnotation(Deprecated.class);
        if (ObjectUtil.isNotNull(annotation)){
            mappingRecordBo.setDeprecated(true);
        }

        Deprecated deprecatedAnnotation = method.getAnnotation(Deprecated.class);
        if (ObjectUtil.isNotNull(deprecatedAnnotation)){
            mappingRecordBo.setDeprecated(true);
        }


        mappingRecordBo.setMappingMethod(method.getName());

        handleHttp(mappingRecordBo);

        handleParams(mappingRecordBo,joinPoint);
    }
    private static void handleHttp(MappingRecordBo mappingRecordBo){
        if (ObjectUtil.isEmpty(mappingRecordBo)){
            return;
        }

        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (ObjectUtil.isEmpty(attributes)){
            return;
        }

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
        if (ObjectUtil.isEmpty(servletRequestAttributes)){
            return;
        }

        HttpServletRequest request = servletRequestAttributes.getRequest();
        if (ObjectUtil.isEmpty(request)){
            return;
        }
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        mappingRecordBo.setHttpMethod(method);
        mappingRecordBo.setBasicUrl(requestURI);


    }
    private static void handleParams(MappingRecordBo mappingRecordBo,JoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        if (ObjectUtil.isNotEmpty(parameterNames)){
            mappingRecordBo.setParamsClass(Arrays.asList(parameterNames));
        }

        Object[] args = joinPoint.getArgs();
        if (ObjectUtil.isEmpty(args)){
            return;
        }
        Map<String,Object> paramMap = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (ObjectUtil.isNull(parameterNames) || ObjectUtil.isNull(parameterNames[i])){
                continue;
            }
            paramMap.put(parameterNames[i],ObjectUtil.isNull(args[i]) ? "null" : args[i].toString());
        }
        mappingRecordBo.setParams(JSON.toJSONString(paramMap));


    }

    private static void appendTokenInfo(MappingRecordBo mappingRecordBo){
        try {
            if (ObjectUtil.isEmpty(mappingRecordBo)){
                return;
            }
            AioUserApi aioUserApi = SpringUtil.getBean(AioUserApi.class);
            if (ObjectUtil.isEmpty(aioUserApi)){
                return;
            }
            AioUser currentUser = aioUserApi.getCurrentUser();
            if (ObjectUtil.isEmpty(currentUser)){
                return;
            }
            if (StringUtils.isNotBlank(currentUser.getUserId())){
                mappingRecordBo.setUserId(currentUser.getUserId());
            }
            if (StringUtils.isNotBlank(currentUser.getUserName())){
                mappingRecordBo.setUserName(currentUser.getUserName());
            }
            if (StringUtils.isNotBlank(currentUser.getCompanyId())){
                mappingRecordBo.setCompanyId(currentUser.getCompanyId());
            }
            if (StringUtils.isNotBlank(currentUser.getCompanyName())){
                mappingRecordBo.setCompanyName(currentUser.getCompanyName());
            }
        }catch (Exception e){
            mappingRecordBo.setUserName("未登录用户");
        }


    }
}
