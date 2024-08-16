package com.aio.runtime.record.log;

import com.aio.runtime.common.info.SystemRuntimeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author lzm
 * @desc 系统启动事件
 * @date 2024/08/13
 */
@Component
@Slf4j
public class AioRuntimeApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        SystemRuntimeInfo.systemStarted();
    }
}
