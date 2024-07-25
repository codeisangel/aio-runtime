package com.aio.runtime.record.log.subscribe.controller;

import com.aio.runtime.record.log.subscribe.domain.params.QuerySubscribeLogParams;
import com.aio.runtime.record.log.subscribe.service.SubscribeLogService;
import com.kgo.flow.common.domain.amis.AmisResult;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzm
 * @desc Mapping日志控制器
 * @date 2024/05/13
 */
@RestController
@Slf4j
@RequestMapping("/runtime/log/subscribe/")
public class SubscribeLogController {
    @Autowired
    private SubscribeLogService subscribeLogService;
    @GetMapping("page")
    public AmisResult getRecordPage(@ModelAttribute QuerySubscribeLogParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = subscribeLogService.getPage(params,page);
        return AmisResult.success(pageResult);
    }
}
