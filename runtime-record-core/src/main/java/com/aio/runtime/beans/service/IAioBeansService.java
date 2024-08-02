package com.aio.runtime.beans.service;

import com.aio.runtime.environment.domain.EnvironmentItemBo;
import com.aio.runtime.environment.domain.QueryEnvironmentParams;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;

public interface IAioBeansService {
    PageResult<EnvironmentItemBo> getPage(QueryEnvironmentParams params, KgoPage page);
    void readBeans();
}
