package com.aio.runtime.subscribe.log;

import ch.qos.logback.classic.LoggerContext;
import com.kgo.framework.basic.integration.user.AioUserApi;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SubscribeAppenderRunner implements ApplicationRunner {
    @Autowired(required = false)
    private AioUserApi aioUserApi;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LoggerContext context = (LoggerContext)StaticLoggerBinder.getSingleton().getLoggerFactory();
        SubscribeErrorLogAppender appender = new SubscribeErrorLogAppender(aioUserApi);
        appender.setContext(context);
        appender.setName("SubscribeErrorLogAppender");
        appender.start();
        ch.qos.logback.classic.Logger logger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(appender);
    }
}
