package com.aio.runtime.log.service;

import com.aio.runtime.log.domain.AioLogBo;
import com.aio.runtime.log.domain.QueryLogParams;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;

public interface AioLogService {
    PageResult<AioLogBo> getPage(QueryLogParams params, KgoPage page);
}
