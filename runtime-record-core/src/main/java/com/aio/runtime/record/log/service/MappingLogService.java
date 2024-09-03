package com.aio.runtime.record.log.service;

import com.aio.runtime.record.log.domain.MappingRecordBo;
import com.aio.runtime.record.log.domain.QueryRecordParams;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;

import java.util.List;

public interface MappingLogService {
    void temporaryStorage(MappingRecordBo mappingRecord);
    List<MappingRecordBo> drainTo();
    void batchSave(List<MappingRecordBo> recordList);
    PageResult searchRecords(QueryRecordParams params, KgoPage page);
    Object getRecordInfo();
}
