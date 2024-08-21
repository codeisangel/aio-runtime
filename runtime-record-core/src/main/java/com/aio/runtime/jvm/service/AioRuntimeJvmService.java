package com.aio.runtime.jvm.service;

import com.aio.runtime.jvm.domain.AioJvmBo;
import com.aio.runtime.jvm.domain.QueryJvmParams;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;

public interface AioRuntimeJvmService {
    PageResult<AioJvmBo> getPage(QueryJvmParams params, KgoPage page);
    void clearStatisticsData();
}
