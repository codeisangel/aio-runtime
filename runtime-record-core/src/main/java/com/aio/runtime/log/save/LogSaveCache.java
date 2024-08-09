package com.aio.runtime.log.save;

import cn.hutool.core.util.ObjectUtil;
import com.aio.runtime.log.domain.AioLogBo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author lzm
 * @desc 日志保存缓存
 * @date 2024/08/08
 */
public class LogSaveCache {
    public static BlockingQueue<AioLogBo> AIO_LOG_QUEUE = new  LinkedBlockingQueue<>(10000);

    public static void addLog(AioLogBo logBo){
        if (ObjectUtil.isNull(logBo)){
            return;
        }
        AIO_LOG_QUEUE.add(logBo);
    }

    public static List<AioLogBo> drainTo() {
        List<AioLogBo> records = new ArrayList<>();
        AIO_LOG_QUEUE.drainTo(records);
        return records;
    }
}
