package com.aio.runtime.beans.service;

import com.aio.runtime.beans.domain.AioBeanVo;
import com.aio.runtime.beans.domain.QueryBeanParams;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;

public interface IAioBeansService {
    PageResult<AioBeanVo> getPage(QueryBeanParams params, KgoPage page);
    void readBeans();
}
