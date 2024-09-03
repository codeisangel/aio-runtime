package com.aio.runtime.record.log.integration;

import cn.aio1024.framework.basic.discover.AioDiscover;
import cn.aio1024.framework.basic.discover.domain.ModuleDetails;
import cn.aio1024.framework.basic.discover.domain.ModuleEnvironmentVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RuntimeRecordModuleDiscover implements AioDiscover {
    @Value("${runtime.version}")
    private String version;
    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public ModuleDetails getDetails() {
        ModuleDetails moduleDetails = new ModuleDetails();
        moduleDetails.setName("系统运行记录");
        moduleDetails.setOpsUrl("/view/runtime");
        moduleDetails.setDesc("系统运行记录");
        moduleDetails.setTag("runtimeLog");
        moduleDetails.setVersion(getVersion());
        moduleDetails.setLogo("/static/runtimeLogo.png");
        return moduleDetails;
    }

    @Override
    public List<ModuleEnvironmentVariable> getEnvironmentVariables() {
        return null;
    }
}
