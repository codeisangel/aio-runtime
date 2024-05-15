package com.aio.runtime.record.log.service;

import com.aio.runtime.record.log.domain.MappingRecordBo;
import com.aio.runtime.record.log.domain.QueryRecordParams;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;

import java.io.IOException;
import java.util.List;

public interface MappingLogService {
    void temporaryStorage(MappingRecordBo mappingRecord);
    List<MappingRecordBo> drainTo();
    void batchSave(List<MappingRecordBo> recordList) throws IOException;
    PageResult searchRecords(QueryRecordParams params, KgoPage page);
}
