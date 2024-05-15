package com.aio.runtime.record.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import cn.hutool.core.lang.Console;

/**
 * @author lzm
 * @desc 日志appender
 * @date 2024/05/11
 */
public class LogLearnAppender extends AppenderBase<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent iLoggingEvent) {

    }

    @Override
    public void start() {
        super.start();
        Console.log("append");
    }
}
