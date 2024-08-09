package com.aio.runtime.log.append;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aio.runtime.log.domain.AioLogBo;
import com.aio.runtime.log.save.LogSaveCache;
import com.kgo.framework.basic.domain.trace.TraceId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.Map;

/**
 * @author lzm
 * @desc 订阅错误日志
 * @date 2024/07/22
 */
@Slf4j
public class AioLogLiteAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent event) {

        Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
        AioLogBo logBo = new AioLogBo();
        logBo.setMdc(mdcPropertyMap);
        logBo.setMessage(event.getFormattedMessage());

        if (ObjectUtil.isNotEmpty(event.getCallerData())){
            StackTraceElement source = event.getCallerData()[0];
            logBo.setMethodName(source.getMethodName());
            logBo.setClassName(source.getClassName());
        }

        logBo.setId(IdUtil.getSnowflakeNextIdStr());
        logBo.setCreateTime(event.getTimeStamp());
        logBo.setThreadName(event.getThreadName());
        logBo.setLevel(event.getLevel().levelStr);

        if (StringUtils.isBlank(TraceId.getTraceId())){
            String traceId = MDC.get("traceId");
            if (StringUtils.isNotBlank(traceId)){
                TraceId.setTraceId(traceId);
            }
        }

        logBo.setTraceId(TraceId.getTraceId());

        if (ObjectUtil.isNotEmpty(event.getMarker())){
            logBo.setMarker(event.getMarker().getName());
        }
        LogSaveCache.addLog(logBo);
    }
}
