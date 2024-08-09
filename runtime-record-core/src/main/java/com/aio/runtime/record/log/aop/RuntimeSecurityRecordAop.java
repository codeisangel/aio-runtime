package com.aio.runtime.record.log.aop;

import com.kgo.aio.framework.basic.spring.utils.ServletUtils;
import com.kgo.framework.basic.adapter.user.AioSecurityAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lzm
 * @desc 控制器请求AOP
 * @date 2024/05/11
 */
@Aspect
//@Component
@Slf4j
public class RuntimeSecurityRecordAop {
    @Autowired
    private AioSecurityAdapter securityAdapter;
    @Pointcut("@annotation(com.kgo.framework.basic.adapter.user.annotations.AioSecurityVerify)")
    public void pointcut() {
    }

    @Before(value="pointcut()")
    public void afterReturning(JoinPoint joinPoint) {
        HttpServletRequest request = ServletUtils.getRequest();
        String token = request.getHeader("Token");
        if (StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }
        if (StringUtils.isBlank(token)){
            log.error("运行时模块，未获取到安全令牌。");
            throw new RuntimeException("未获取安全令牌");
        }
        /*if (!securityAdapter.verifyToken(token)){
            log.error("运行时模块，安全令牌[ {} ]已失效。",token);
            throw new RuntimeException("未获取安全令牌");
        }*/
    }

}
