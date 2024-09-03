package com.aio.runtime.subscribe.service;

import com.aio.runtime.subscribe.domain.SubscribeLogVo;
import com.aio.runtime.subscribe.domain.params.QuerySubscribeLogParams;
import com.aio.runtime.subscribe.domain.params.UpdateSubscribeLogStatusParams;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;

public interface SubscribeLogService {
   PageResult<SubscribeLogVo> getPage(QuerySubscribeLogParams params , KgoPage page);
   void updateSubscribeLogStatusToHandled(UpdateSubscribeLogStatusParams params);
}
