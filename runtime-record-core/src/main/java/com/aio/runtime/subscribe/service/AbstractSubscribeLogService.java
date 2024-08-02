package com.aio.runtime.subscribe.service;

import com.aio.runtime.subscribe.domain.SubscribeLogBo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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
    private ScheduledExecutorService executorSubscribeLogService = Executors.newScheduledThreadPool(2);
    public AbstractSubscribeLogService(){
        Runnable task = () -> {
            List<SubscribeLogBo> recordBos = drainTo();
            batchSave(recordBos);
        };
        executorSubscribeLogService.scheduleAtFixedRate(task, 30, 10, TimeUnit.SECONDS);
    }
    private List<SubscribeLogBo> drainTo() {
        List<SubscribeLogBo> records = new ArrayList<>();
        RECORD_QUEUE.drainTo(records);
        return records;
    }
    public abstract void batchSave(List<SubscribeLogBo> recordBos);
}
