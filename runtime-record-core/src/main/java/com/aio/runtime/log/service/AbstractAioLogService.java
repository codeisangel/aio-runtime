package com.aio.runtime.log.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aio.runtime.log.domain.AioLogBo;
import com.aio.runtime.log.save.LogSaveCache;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author lzm
 * @desc 日志管理抽象类
 * @date 2024/08/08
 */
@Slf4j
public abstract class AbstractAioLogService implements AioLogService {
    private ScheduledExecutorService logThreadPool = Executors.newScheduledThreadPool(5);

    public AbstractAioLogService(){
        Runnable task = () -> {
            long start = System.currentTimeMillis();
            List<AioLogBo> aioLogList = LogSaveCache.drainTo();
            batchSaveLogs(aioLogList);
            if (ObjectUtil.isNotEmpty(aioLogList)){
                String logStr = StrUtil.format("存储日志任务，执行时间[ {} ] ,日志共 [ {} ] 行 , 耗时[ {} ]", DateUtil.now(),aioLogList.size(),System.currentTimeMillis()-start);
                 FileUtil.appendUtf8Lines(Arrays.asList(logStr), "D:\\home\\record\\log\\logSave.log");
            }


        };
        Runnable clearTask = () -> {
           clearLogs();
        };
        logThreadPool.scheduleAtFixedRate(task, 30, 300, TimeUnit.MILLISECONDS);
        logThreadPool.scheduleAtFixedRate(clearTask, 1, 2, TimeUnit.HOURS);
    }
    public abstract void batchSaveLogs(List<AioLogBo> aioLogList);
    public abstract void clearLogs();


}
