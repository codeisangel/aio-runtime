package com.guodun.aio.document.user;

import cn.hutool.core.util.IdUtil;
import com.kgo.framework.basic.domain.trace.TraceId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lizhenming
 * @desc:
 * @date 2023/7/31 18:12
 */
@Component
@Slf4j
public class TraceIdInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String snowflakeNextIdStr = IdUtil.getSnowflakeNextIdStr();
        TraceId.setTraceId(snowflakeNextIdStr);
        return true;
    }
}
