package com.aio.runtime.mappings.service;

import com.aio.runtime.mappings.domain.AioMappingVo;
import com.aio.runtime.mappings.domain.QueryMappingParams;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;

public interface IAioMappingService {
    PageResult<AioMappingVo> getPage(QueryMappingParams params, KgoPage page);
}
