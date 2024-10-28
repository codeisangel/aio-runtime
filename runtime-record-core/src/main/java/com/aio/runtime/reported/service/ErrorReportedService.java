package com.aio.runtime.reported.service;

import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import com.aio.runtime.reported.domain.params.AddErrorParams;
import com.aio.runtime.reported.domain.params.QueryErrorParams;

public interface ErrorReportedService {
    void addError(AddErrorParams params);
    PageResult getPage(QueryErrorParams params, KgoPage page);
}
