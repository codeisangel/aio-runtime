package com.aio.runtime.record.log.subscribe.service;

import com.aio.runtime.record.log.subscribe.domain.SubscribeLogVo;
import com.aio.runtime.record.log.subscribe.domain.params.QuerySubscribeLogParams;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;

public interface SubscribeLogService {
   PageResult<SubscribeLogVo> getPage(QuerySubscribeLogParams params , KgoPage page);
}
