package com.aio.runtime.record.log;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import com.aio.runtime.common.info.SystemRuntimeInfo;
import cn.aio1024.framework.basic.domain.trace.TraceId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

import java.util.Date;

/**
 * @author lzm
 * @desc 系统启动事件
 * @date 2024/08/13
 */

@Slf4j
public class AioRuntimeApplicationStartingEventListener implements ApplicationListener<ApplicationStartingEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        TraceId.setTraceId("start_"+ DateUtil.format(new Date(),"yyyyMMdd日HH时mm分ss秒"));
        SystemRuntimeInfo.systemStarting();
        Console.log("#### 系统运行时模块启动 ####");
    }
}
