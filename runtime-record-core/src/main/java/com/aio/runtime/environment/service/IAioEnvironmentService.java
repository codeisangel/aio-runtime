package com.aio.runtime.environment.service;

import com.aio.runtime.environment.domain.EnvironmentItemBo;
import com.aio.runtime.environment.domain.QueryEnvironmentParams;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;

public interface IAioEnvironmentService {
    PageResult<EnvironmentItemBo> getPage(QueryEnvironmentParams params, KgoPage page);
    void readEnvironments();
}
