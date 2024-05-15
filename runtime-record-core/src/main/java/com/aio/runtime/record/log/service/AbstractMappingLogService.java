package com.aio.runtime.record.log.service;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.aio.runtime.record.log.domain.MappingRecordBo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractMappingLogService implements MappingLogService{
    public static BlockingQueue<MappingRecordBo> RECORD_QUEUE = new LinkedBlockingQueue<>(10000);;

    public AbstractMappingLogService(){
        CronUtil.schedule("*/2 * * * * *", new Task() {
            @Override
            public void execute() {
                List<MappingRecordBo> recordBos = drainTo();
                try {
                    batchSave(recordBos);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
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
