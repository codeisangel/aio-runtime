package com.aio.runtime.beans.service;

import com.aio.runtime.beans.domain.AioBeanVo;
import com.aio.runtime.beans.domain.QueryBeanParams;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;

public interface IAioBeansService {
    PageResult<AioBeanVo> getPage(QueryBeanParams params, KgoPage page);
    void readBeans();
}
