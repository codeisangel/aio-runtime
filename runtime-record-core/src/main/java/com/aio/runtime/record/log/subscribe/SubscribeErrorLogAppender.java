package com.aio.runtime.record.log.subscribe;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aio.runtime.record.log.subscribe.domain.SubscribeLogBo;
import com.aio.runtime.record.log.subscribe.domain.enums.SubscibeHandleStatusEnum;
import com.aio.runtime.record.log.subscribe.service.AbstractSubscribeLogService;
import com.kgo.framework.basic.domain.trace.TraceId;
import com.kgo.framework.basic.integration.user.AioUserApi;
import com.kgo.framework.basic.integration.user.domain.AioUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.slf4j.Marker;

import java.util.Date;

/**
 * @author lzm
 * @desc 订阅错误日志
 * @date 2024/07/22
 */
@Slf4j
public class SubscribeErrorLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private final AioUserApi userApi;
    public SubscribeErrorLogAppender(AioUserApi userApi){
        this.userApi =userApi;
    }
    @Override
    protected void append(ILoggingEvent event) {

        Marker marker = event.getMarker();

        if (ObjectUtil.isNull(marker)){
            return;
        }

        if (marker instanceof SubscribeMarker){

            StackTraceElement source = event.getCallerData()[0];
            String className = source.getClassName();
            String methodName = source.getMethodName();

            SubscribeLogBo logBo = new SubscribeLogBo();
            logBo.setSubscribeName(marker.getName());
            logBo.setId(IdUtil.getSnowflakeNextIdStr());
            logBo.setMessage(event.getFormattedMessage());
            logBo.setClassName(className);
            logBo.setMethodName(methodName);
            logBo.setCreateTime(new Date(event.getTimeStamp()));
            AioUser currentUser = userApi.getCurrentUser();
            if (ObjectUtil.isNotEmpty(currentUser)){
                logBo.setCompanyId(currentUser.getCompanyId());
                logBo.setUserId(currentUser.getUserId());
            }
            logBo.setHandleStatus(SubscibeHandleStatusEnum.UN_HANDLED.getStatus());

            if (StringUtils.isBlank(TraceId.getTraceId())){
                String traceId = MDC.get("traceId");
                if (StringUtils.isNotBlank(traceId)){
                    TraceId.setTraceId(traceId);
                }
            }
            logBo.setTraceId(TraceId.getTraceId());
            AbstractSubscribeLogService.addRecord(logBo);
        }

    }
}
