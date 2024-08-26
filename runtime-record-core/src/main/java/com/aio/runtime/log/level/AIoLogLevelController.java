package com.aio.runtime.log.level;

import cn.hutool.core.util.ObjectUtil;
import com.aio.runtime.log.domain.SetLogLevelParams;
import com.kgo.flow.common.domain.amis.AmisResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lzm
 * @desc 日志级别
 * @date 2024/08/09
 */
@RestController
@Slf4j
@RequestMapping("/runtime/aio/log/level")
@ConditionalOnClass(LoggersEndpoint.class)
public class AIoLogLevelController {
    @Autowired(required = false)
    private LoggersEndpoint loggersEndpoint;
    @Autowired(required = false)
    private LoggingSystem loggingSystem;
    @GetMapping("list")
    public AmisResult getLogList(@RequestParam(required = false) String name,@RequestParam(required = false) String configuredLevel){
        Collection<LoggerConfiguration> configurations = this.loggingSystem.getLoggerConfigurations();
        List<LoggerConfiguration> configurationList = new ArrayList<>();
        for (LoggerConfiguration configuration : configurations) {
            if (StringUtils.isNotBlank(name) && (!StringUtils.contains(configuration.getName(),name))){
                continue;
            }
            if (StringUtils.isNotBlank(configuredLevel)) {
                if (ObjectUtil.isNull(configuration.getConfiguredLevel())){
                    continue;
                }
                if (!StringUtils.equals(configuration.getConfiguredLevel().name(),configuredLevel)){
                    continue;
                }
            }
            configurationList.add(configuration);
        }
        return AmisResult.success(configurationList);
    }
    @PutMapping
    public AmisResult setLogLevel(@RequestBody SetLogLevelParams log){
        loggersEndpoint.configureLogLevel(log.getName(), log.getConfiguredLevel());
        return AmisResult.successMsg("设置日志级别成功");
    }
}
