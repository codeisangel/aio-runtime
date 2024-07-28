package com.aio.runtime.environment.service;

import com.aio.runtime.environment.domain.EnvironmentItemBo;
import com.aio.runtime.environment.domain.QueryEnvironmentParams;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;

public interface IAioEnvironmentService {
    PageResult<EnvironmentItemBo> getPage(QueryEnvironmentParams params, KgoPage page);
    void readEnvironments();
}
