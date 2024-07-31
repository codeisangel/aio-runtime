package com.aio.runtime.record.log.aop;

import cn.hutool.core.util.ObjectUtil;
import com.aio.runtime.mappings.statistic.MappingVisitStatisticsUtils;
import com.aio.runtime.record.log.controller.MappingLogController;
import com.aio.runtime.record.log.domain.MappingRecordBo;
import com.aio.runtime.record.log.service.MappingLogService;
import com.aio.runtime.record.log.utils.MappingRecordUtils;
import com.alibaba.fastjson.JSON;
import com.kgo.flow.common.domain.amis.AmisResult;
import com.kgo.framework.basic.domain.trace.TraceId;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lzm
 * @desc 控制器请求AOP
 * @date 2024/05/11
 */
@Aspect
@Component
@Slf4j
public class ControllerRecordAop {
    @Autowired
    private MappingLogService mappingLogService;
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void pointcut() {
    }


    @AfterReturning(pointcut = "pointcut()",returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        if (joinPoint.getTarget() instanceof MappingLogController) {
            return;
        }
        MappingRecordBo builder = MappingRecordUtils.builder(joinPoint);
        if (ObjectUtil.isNotNull(result)){
            builder.setResult(result.toString());
        }
        MappingVisitStatisticsUtils.count(builder.getMappingClass(), builder.getMappingMethod());
        builder.setSuccess(true);
        mappingLogService.temporaryStorage(builder);
        handleReturn(result);
    }

    @AfterThrowing(pointcut = "pointcut()",throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Throwable e){
        MappingRecordBo builder = MappingRecordUtils.builder(joinPoint);
        builder.setSuccess(false);
        MappingVisitStatisticsUtils.count(builder.getMappingClass(), builder.getMappingMethod());
        builder.setStackTrace(JSON.toJSONString(e.getStackTrace()));
        builder.setExceptionMsg(e.getMessage());
        builder.setThrowable(e.getClass().getName());
        mappingLogService.temporaryStorage(builder);
    }
    private void handleReturn(Object result){
        if (ObjectUtil.isEmpty(result)){
            return;
        }
        if (result instanceof AmisResult) {
            ((AmisResult<?>) result).setTraceId(TraceId.getTraceId());
        }
    }
}
