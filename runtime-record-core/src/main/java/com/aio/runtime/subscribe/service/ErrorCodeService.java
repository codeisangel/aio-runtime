package com.aio.runtime.subscribe.service;

import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import com.aio.runtime.subscribe.domain.bo.ErrorCodeBo;
import com.aio.runtime.subscribe.domain.params.AddErrorCodeParams;
import com.aio.runtime.subscribe.domain.params.QueryErrorCodeParams;

/**
 * 错误码
 */
public interface ErrorCodeService {
    PageResult<ErrorCodeBo> getPage(QueryErrorCodeParams params , KgoPage page);

    void addErrorCode(AddErrorCodeParams params);
    void updateErrorCode(ErrorCodeBo params);

    void deleteErrorCode(String id);
}
