package com.aio.runtime.security.aop;

import cn.aio1024.aio.framework.basic.spring.utils.ServletUtils;
import cn.aio1024.framework.basic.adapter.user.AioSecurityAdapter;
import com.aio.runtime.domain.exception.RuntimeSecurityUnAuthException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lzm
 * @desc 控制器请求AOP
 * @date 2024/05/11
 */
@Aspect
@Component
@Slf4j
public class RuntimeControllerSecurityAop {
    @Autowired
    private AioSecurityAdapter securityAdapter;

    @Pointcut("execution(public * com.aio.runtime.beans.controller.IAioBeansController.*(..)) || " +
            "execution(public * com.aio.runtime.cache.controller.AioCacheManageController.*(..)) || " +
            "execution(public * com.aio.runtime.common.AioRuntimeCommonController.*(..))  || " +
            "execution(public * com.aio.runtime.environment.controller.AioEnvironmentController.*(..))  || " +
            "execution(public * com.aio.runtime.jvm.controller.AioJvmController.*(..))  || " +
            "execution(public * com.aio.runtime.subscribe.controller.SubscribeLogController.*(..))  || " +
            "execution(public * com.aio.runtime.log.controller.AioLogController.*(..))  || " +
            "execution(public * com.aio.runtime.record.log.controller.MappingLogController.*(..))  || " +
            "execution(public * com.aio.runtime.mappings.controller.AioMappingController.*(..))")
    public void allMethodsPointcut() {
    }


    @Before("allMethodsPointcut()")
    public void afterReturning(JoinPoint joinPoint) {
        HttpServletRequest request = ServletUtils.getRequest();
        String token = request.getHeader("RUNTIME-TOKEN");
        if (StringUtils.isBlank(token)){
            token = request.getParameter("runtimeToken");
        }
        boolean verify = securityAdapter.verifyToken(token);
        log.info("权限AOP拦截器 ： {}  token [ {} ] , 校验结果 ： {} ",request.getRequestURI() , token ,verify);
        handleVerifyResult(verify);
    }
    private void handleVerifyResult(boolean verify){
        if (verify){
            return;
        }
        throw new RuntimeSecurityUnAuthException();
    }

}
