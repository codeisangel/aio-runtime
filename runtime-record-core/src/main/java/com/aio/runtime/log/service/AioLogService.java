package com.aio.runtime.log.service;

import com.aio.runtime.log.domain.AioLogBo;
import com.aio.runtime.log.domain.QueryLogParams;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;

public interface AioLogService {
    PageResult<AioLogBo> getPage(QueryLogParams params, KgoPage page);
}
