package com.aio.runtime.common;

import cn.hutool.system.*;
import com.aio.runtime.common.info.SystemRuntimeInfo;
import com.kgo.flow.common.domain.amis.AmisResult;
import com.kgo.flow.common.domain.constants.ProjectWorkSpaceConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzm
 * @desc 运行时控制器
 * @date 2024/08/16
 */
@RestController
@Slf4j
public class AioRuntimeCommonController {
    @Value("${version}")
    private String version;

    @Value(ProjectWorkSpaceConstants.CONFIG_PATH_SPEL)
    private String projectWorkspace;

    @Autowired(required = false)
    private MetricsEndpoint metricsEndpoint;

    @GetMapping("/runtime/aio/version")
    public AmisResult getVersion(){
        return AmisResult.success(version);
    }
    @GetMapping("/runtime/aio/workspace")
    public AmisResult getWorkspace(){
        return AmisResult.success(projectWorkspace);
    }

    @GetMapping("/runtime/aio/system/starting/time")
    public AmisResult getSystemStartingTime(){
        return AmisResult.success(SystemRuntimeInfo.getSystemStartingTime());
    }

    @GetMapping("/runtime/aio/system/started/time")
    public AmisResult getSystemStartedTime(){
        return AmisResult.success(SystemRuntimeInfo.getSystemStartedTime());
    }

    @GetMapping("/runtime/aio/system/hostInfo")
    public AmisResult getSystemHost(){
        HostInfo hostInfo = SystemUtil.getHostInfo();
        OsInfo osInfo = SystemUtil.getOsInfo();
        String name = osInfo.getName();
        return AmisResult.success(hostInfo);
    }

    @GetMapping("/runtime/aio/system/pid")
    public AmisResult getPId(){
        long currentPID = SystemUtil.getCurrentPID();
        return AmisResult.success(currentPID);
    }
    @GetMapping("/runtime/aio/system/os")
    public AmisResult getSystemOs(){
        OsInfo osInfo = SystemUtil.getOsInfo();
        return AmisResult.success(osInfo);
    }
    @GetMapping("/runtime/aio/system/jvmInfo")
    public AmisResult getSystemJvmInfo(){
        JvmInfo jvmInfo = SystemUtil.getJvmInfo();
        return AmisResult.success(jvmInfo);
    }

    @GetMapping("/runtime/aio/system/jvmSpecInfo")
    public AmisResult getSystemJvmSpecInfo(){
        JvmSpecInfo jvmSpecInfo = SystemUtil.getJvmSpecInfo();
        return AmisResult.success(jvmSpecInfo);
    }


}
