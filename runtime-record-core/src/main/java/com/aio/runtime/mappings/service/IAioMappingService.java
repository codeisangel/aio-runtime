package com.aio.runtime.mappings.service;

import com.aio.runtime.mappings.domain.AioMappingBo;
import com.aio.runtime.mappings.domain.AioMappingVo;
import com.aio.runtime.mappings.domain.QueryMappingParams;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;

public interface IAioMappingService {
    PageResult<AioMappingVo> getPage(QueryMappingParams params, KgoPage page);
    AioMappingBo getMapping(String className,String methodName);
    boolean updateMappingById(AioMappingBo mappingBo);
}
