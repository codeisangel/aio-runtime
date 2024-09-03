package com.aio.runtime.jvm.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import com.aio.runtime.jvm.domain.AioJvmBo;
import com.aio.runtime.jvm.domain.QueryJvmParams;
import com.aio.runtime.jvm.service.AioRuntimeJvmService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import cn.aio1024.framework.basic.domain.amis.AmisResult;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lzm
 * @desc jvm控制器
 * @date 2024/08/20
 */

@RestController
@Slf4j
@RequestMapping("/runtime/aio/jvm")
public class AioJvmController {

    @Autowired
    private AioRuntimeJvmService jvmService;
    @GetMapping("page")
    public AmisResult getEnvironmentItemPage(@ModelAttribute QueryJvmParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = jvmService.getPage(params, page);
        return AmisResult.success(pageResult);
    }
    @DeleteMapping("clear")
    public AmisResult clearJvmData(){
        jvmService.clearStatisticsData();
        return AmisResult.successMsg("清理完成");
    }
    @GetMapping("memory/chart/line")
    public AmisResult getLineChart(@ModelAttribute QueryJvmParams params , @ModelAttribute KgoPage page){
        page.setPageSize(1000);
        PageResult<AioJvmBo> pageResult = jvmService.getPage(params, page);
        List<AioJvmBo> list = pageResult.getList();
        List<Double> memoryMaxList = new ArrayList<>();
        List<Double> memoryUsedList = new ArrayList<>();
        List<Double> memoryCommittedList = new ArrayList<>();
        List<Integer> threadDaemonList = new ArrayList<>();
        List<Integer> threadPeakList = new ArrayList<>();
        List<Integer> threadLiveList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();
         for (AioJvmBo jvmBo : list) {
            dataList.add(DateUtil.formatDateTime(jvmBo.getCreateTime()));
            memoryMaxList.add(Double.valueOf(jvmBo.getJvmMemoryMax()));
            memoryUsedList.add(Double.valueOf(jvmBo.getJvmMemoryUsed()));
            memoryCommittedList.add(Double.valueOf(jvmBo.getJvmMemoryCommitted()));
            threadDaemonList.add(Integer.valueOf(jvmBo.getJvmThreadsDaemon()));
            threadPeakList.add(Integer.valueOf(jvmBo.getJvmThreadsPeak()));
            threadLiveList.add(Integer.valueOf(jvmBo.getJvmThreadsLive()));
        }

        String memoryChartLineStr = ResourceUtil.readStr("chart/MemoryChartLine.json", CharsetUtil.CHARSET_UTF_8);
        JSONObject memoryChartLine = JSON.parseObject(memoryChartLineStr);
        JSONPath.set(memoryChartLine,"xAxis.data",dataList);
        JSONPath.set(memoryChartLine,"series[0].data",memoryUsedList);
        JSONPath.set(memoryChartLine,"series[1].data",memoryCommittedList);

        String threadChartLineStr = ResourceUtil.readStr("chart/ThreadChartLine.json", CharsetUtil.CHARSET_UTF_8);
        JSONObject threadChartLine = JSON.parseObject(threadChartLineStr);
        JSONPath.set(threadChartLine,"xAxis.data",dataList);
        JSONPath.set(threadChartLine,"series[0].data",threadDaemonList);
        JSONPath.set(threadChartLine,"series[1].data",threadPeakList);
        JSONPath.set(threadChartLine,"series[2].data",threadLiveList);
        resultMap.put("memory",memoryChartLine);
        resultMap.put("thread",threadChartLine);


        log.debug("日期数据数量[ {} ] 内存使用数量[ {} ] , 已分配内存数据[ {} ]",dataList.size(),memoryUsedList.size(),memoryCommittedList.size());
        return AmisResult.success(resultMap);
    }
}
