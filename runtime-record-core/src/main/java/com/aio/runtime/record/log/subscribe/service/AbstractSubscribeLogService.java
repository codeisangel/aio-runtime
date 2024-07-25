package com.aio.runtime.record.log.subscribe.service;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.aio.runtime.record.log.subscribe.domain.SubscribeLogBo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author lzm
 * @desc 订阅日志服务抽象类
 * @date 2024/07/24
 */
public abstract class AbstractSubscribeLogService implements SubscribeLogService{
    private static BlockingQueue<SubscribeLogBo> RECORD_QUEUE = new LinkedBlockingQueue<>(10000);;
    public static void addRecord(SubscribeLogBo logBo){
        RECORD_QUEUE.add(logBo);
    }
    public AbstractSubscribeLogService(){
        CronUtil.schedule("*/5 * * * * *", new Task() {
            @Override
            public void execute() {
                List<SubscribeLogBo> recordBos = drainTo();
                batchSave(recordBos);
            }
        });
        CronUtil.setMatchSecond(true);
    }
    private List<SubscribeLogBo> drainTo() {
        List<SubscribeLogBo> records = new ArrayList<>();
        RECORD_QUEUE.drainTo(records);
        return records;
    }
    public abstract void batchSave(List<SubscribeLogBo> recordBos);
}
