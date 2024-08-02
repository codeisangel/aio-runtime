package com.guodun.aio.document;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.aio.runtime.subscribe.log.SubscribeMarker;
import com.kgo.flow.common.domain.amis.AmisResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lzm
 * @desc 订阅控制器
 * @date 2024/07/24
 */
@RestController
@Deprecated
@Slf4j
@RequestMapping("/test/subscribe")
public class TestSubLogController {
    @PostMapping("create")
    public AmisResult crateSubscribes(){
        List<File> files = FileUtil.loopFiles("D:\\test\\随机文本\\random");
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            List<String> lines = FileUtil.readUtf8Lines(file);
            for (String line : lines) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.error(SubscribeMarker.getMarker(RandomUtil.randomStringUpper(3)), StringUtils.trim(line));
            }
        }
        return AmisResult.successMsg("创建订阅日志");
    }

    @PostMapping("create2")
    public AmisResult crateSubscribes(@RequestParam Integer total){
            List<String> lines = FileUtil.readUtf8Lines("D:\\test\\随机文本\\random\\卫斯理系列@003妖火.txt");
            AtomicInteger count = new AtomicInteger(0);
            for (String line : lines) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }

                log.error(SubscribeMarker.getMarker(RandomUtil.randomStringUpper(3)), StringUtils.trim(line));
                if (count.addAndGet(1) > total) {
                    break;
                }
            }

        return AmisResult.successMsg("创建订阅日志");
    }
}
