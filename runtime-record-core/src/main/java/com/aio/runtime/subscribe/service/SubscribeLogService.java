package com.aio.runtime.subscribe.service;

import com.aio.runtime.subscribe.domain.SubscribeLogVo;
import com.aio.runtime.subscribe.domain.params.QuerySubscribeLogParams;
import com.aio.runtime.subscribe.domain.params.UpdateSubscribeLogStatusParams;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;

public interface SubscribeLogService {
   PageResult<SubscribeLogVo> getPage(QuerySubscribeLogParams params , KgoPage page);
   void updateSubscribeLogStatusToHandled(UpdateSubscribeLogStatusParams params);
}
