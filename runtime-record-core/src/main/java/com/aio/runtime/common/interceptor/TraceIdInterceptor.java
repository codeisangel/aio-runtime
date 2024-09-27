package com.aio.runtime.common.interceptor;

import cn.aio1024.framework.basic.domain.trace.TraceId;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lzm
 * @desc:
 * @date 2023/7/31 18:12
 */
public class TraceIdInterceptor implements HandlerInterceptor {
    private final static String TRACE_ID_HEADER = "Trace-Id";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (StringUtils.isBlank(traceId)){
            traceId = IdUtil.getSnowflakeNextIdStr();
        }
        TraceId.setTraceId(traceId);
        response.addHeader(TRACE_ID_HEADER,traceId);
        return true;
    }
}
