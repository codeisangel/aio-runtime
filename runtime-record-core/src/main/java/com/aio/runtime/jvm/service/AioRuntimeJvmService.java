package com.aio.runtime.jvm.service;

import com.aio.runtime.jvm.domain.AioJvmBo;
import com.aio.runtime.jvm.domain.QueryJvmParams;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;

public interface AioRuntimeJvmService {
    PageResult<AioJvmBo> getPage(QueryJvmParams params, KgoPage page);
    void clearStatisticsData();
}
