package com.guodun.aio.document;

import cn.hutool.core.date.DateUtil;
import com.aio.runtime.record.log.subscribe.SubscribeMarker;
import com.kgo.flow.common.domain.amis.AmisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzm
 * @desc 订阅控制器
 * @date 2024/07/24
 */
@RestController
@Slf4j
@RequestMapping("/test/subscribe")
public class TestSubLogController {
    @PostMapping("create")
    public AmisResult crateSubscribes(@RequestParam Integer total){

        for (int i = 0; i < total; i++) {
            log.error(SubscribeMarker.getMarker(), "这是一个测试的订阅日志 ： {} ", DateUtil.now());
        }
        return AmisResult.successMsg("创建订阅日志");
    }
}
