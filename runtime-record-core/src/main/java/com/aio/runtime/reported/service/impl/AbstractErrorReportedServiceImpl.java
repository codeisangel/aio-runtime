package com.aio.runtime.reported.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aio.runtime.reported.domain.dao.ErrorReportDo;
import com.aio.runtime.reported.domain.params.AddErrorParams;
import com.aio.runtime.reported.service.ErrorReportedService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author lzm
 * @desc 抽象类
 * @date 2024/10/26
 */
public abstract class AbstractErrorReportedServiceImpl implements ErrorReportedService {
    public static BlockingQueue<ErrorReportDo>  ERROR_REPORTED_QUEUE = new LinkedBlockingQueue<>(2000);

    @Override
    public void addError(AddErrorParams params){
        if (ObjectUtil.isNull(params)){
            return;
        }
        if (ERROR_REPORTED_QUEUE.size() >= 1990){
            drainTo();
            batchSaveError(drainTo());
        }
        ErrorReportDo reportDo = Convert.convert(ErrorReportDo.class,params);
        reportDo.setCreateTime(new Date());
        reportDo.setId(IdUtil.getSnowflakeNextIdStr());
        ERROR_REPORTED_QUEUE.add(reportDo);

    }
    @Scheduled(cron = "0/10 * * * * ? ")
    private void batchBatchSaveErrorTask(){
        List<ErrorReportDo> errorReportDos = drainTo();
        if (ObjectUtil.isEmpty(errorReportDos)){
            return;
        }
        batchSaveError(errorReportDos);
    }
    public static List<ErrorReportDo> drainTo() {
        List<ErrorReportDo> records = new ArrayList<>();
        ERROR_REPORTED_QUEUE.drainTo(records);
        return records;
    }
    public abstract void batchSaveError(List<ErrorReportDo> recordList);
}
