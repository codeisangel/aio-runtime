package com.aio.runtime.common.interceptor;

import cn.aio1024.framework.basic.domain.trace.TraceId;
import cn.hutool.core.util.IdUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzm
 * @desc:
 * @date 2023/7/31 18:12
 */
public class TraceIdInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String snowflakeNextIdStr = IdUtil.getSnowflakeNextIdStr();
        TraceId.setTraceId(snowflakeNextIdStr);
        return true;
    }
}
