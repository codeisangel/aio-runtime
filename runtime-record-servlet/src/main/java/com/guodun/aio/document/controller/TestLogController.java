package com.guodun.aio.document.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.aio.runtime.subscribe.log.SubscribeMarker;
import cn.aio1024.framework.basic.domain.amis.AmisResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lzm
 * @desc 测试创建日志
 * @date 2024/08/09
 */
@RestController
@Slf4j
@RequestMapping("/test/log")
public class TestLogController {
    @PostMapping("subscribe")
    public AmisResult crateSubscribeLog(@RequestParam String marker){
        List<String> lines = FileUtil.readUtf8Lines("D:\\test\\随机文本\\random\\卫斯理系列@004蓝血人.txt");
        int i = RandomUtil.randomInt(1, lines.size()-1);
        SubscribeMarker subscribeMarker = SubscribeMarker.getMarker(marker);
        log.error(subscribeMarker, "第 {} 行 {}",i,StringUtils.trim(lines.get(i)));

        return AmisResult.successMsg("创建订阅日志");
    }
    @PostMapping("create")
    public AmisResult crateSubscribes(@RequestParam Integer total,@RequestParam String level){
        List<String> lines = FileUtil.readUtf8Lines("D:\\test\\随机文本\\random\\卫斯理系列@004蓝血人.txt");
        if (lines.size() < total){
            List<String> lines1 = FileUtil.readUtf8Lines("D:\\test\\随机文本\\random\\卫斯理系列@003妖火.txt");
            lines.addAll(lines1);
            if (lines.size() < total){
                lines1 = FileUtil.readUtf8Lines("D:\\test\\随机文本\\random\\卫斯理系列@005透明光.txt");
                lines.addAll(lines1);
            }
        }
        AtomicInteger count = new AtomicInteger(0);
        MDC.put("logCreate", IdUtil.nanoId());
        MDC.put("testMdc", RandomUtil.randomStringUpper(10));
        int start = RandomUtil.randomInt(0, lines.size() - total-10);
        SubscribeMarker marker = SubscribeMarker.getMarker(RandomUtil.randomStringUpper(3));
        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i);
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (count.addAndGet(1) > total) {
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (StringUtils.equalsIgnoreCase(level, Level.ERROR.name())){
                log.error(marker, "第 {} 行 {}",i,StringUtils.trim(line));
            }else if (StringUtils.equalsIgnoreCase(level, Level.INFO.name())){
                log.info(marker, "第 {} 行 {}",i,StringUtils.trim(line));
            }else if (StringUtils.equalsIgnoreCase(level, Level.DEBUG.name())){
                log.debug(marker, "第 {} 行 {}",i,StringUtils.trim(line));
            }else if (StringUtils.equalsIgnoreCase(level, Level.WARN.name())){
                log.warn(marker, "第 {} 行 {}",i,StringUtils.trim(line));
            }
        }

        return AmisResult.successMsg("创建订阅日志");
    }

    @PostMapping("create2")
    public AmisResult crateSubscribes2(@RequestParam Integer start,@RequestParam Integer total,@RequestParam String level){


        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                AtomicInteger count = new AtomicInteger(0);
                List<File> files = FileUtil.loopFiles("D:\\test\\随机文本\\random\\");
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }

                    printLog(count,file,level,start,total);
                }
                log.error("日志插入数量 ： {} ",count.get());
            }
        });

        return AmisResult.successMsg("创建订阅日志");
    }
    private void printLog(AtomicInteger count , File file,String level , int start, int total){
        List<String> lines = FileUtil.readUtf8Lines(file);
        MDC.put("logCreate", IdUtil.nanoId());
        MDC.put("testMdc", RandomUtil.randomStringUpper(10));

        for (int i = 0; i < lines.size(); i++) {

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // SubscribeMarker marker = SubscribeMarker.getMarker(RandomUtil.randomStringUpper(3));
            String line = lines.get(i);
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (count.addAndGet(1) > total) {
                break;
            }
            if (count.get() < start){
                continue;
            }

            SubscribeMarker marker = null;
            if (StringUtils.equalsIgnoreCase(level, Level.ERROR.name())){
                log.error(marker, "第 {} 条,第 {} 行 {}",count.get(),i,StringUtils.trim(line));
            }else if (StringUtils.equalsIgnoreCase(level, Level.INFO.name())){
                log.info(marker, "第 {} 条,第 {} 行 {}",count.get(),i,StringUtils.trim(line));
            }else if (StringUtils.equalsIgnoreCase(level, Level.DEBUG.name())){
                log.debug(marker, "第 {} 条,第 {} 行 {}",count.get(),i,StringUtils.trim(line));
            }else if (StringUtils.equalsIgnoreCase(level, Level.WARN.name())){
                log.warn(marker, "第 {} 条,第 {} 行 {}",count.get(),i,StringUtils.trim(line));
            }
        }
    }
}
