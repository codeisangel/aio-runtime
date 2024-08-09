package com.aio.runtime.log.append;

import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author lzm
 * @desc 订阅错误日志
 * @date 2024/07/22
 */
@Component
@Slf4j
public class AioLogAppenderRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        LoggerContext context = (LoggerContext)StaticLoggerBinder.getSingleton().getLoggerFactory();
        AioLogLiteAppender appender = new AioLogLiteAppender();
        appender.setContext(context);
        appender.setName("aioLogLiteAppender");
        appender.start();
        ch.qos.logback.classic.Logger logger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(appender);
    }
}
