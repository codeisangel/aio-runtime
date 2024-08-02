package com.aio.runtime.record.log.service;

import com.aio.runtime.record.log.domain.MappingRecordBo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public abstract class AbstractMappingLogService implements MappingLogService{
    public static BlockingQueue<MappingRecordBo> RECORD_QUEUE = new LinkedBlockingQueue<>(10000);
    private ScheduledExecutorService executorMappingLogService = Executors.newScheduledThreadPool(1);
    public AbstractMappingLogService(){
        Runnable task = () -> {
            List<MappingRecordBo> recordBos = drainTo();
            batchSave(recordBos);
        };
        executorMappingLogService.scheduleAtFixedRate(task, 30, 2, TimeUnit.SECONDS);

    }
    @Override
    public void temporaryStorage(MappingRecordBo mappingRecord) {
        RECORD_QUEUE.add(mappingRecord);
    }

    @Override
    public List<MappingRecordBo> drainTo() {
        List<MappingRecordBo> records = new ArrayList<>();
        RECORD_QUEUE.drainTo(records);
        return records;
    }
}
