package com.aio.runtime.subscribe.controller;

import com.aio.runtime.subscribe.domain.params.QuerySubscribeLogParams;
import com.aio.runtime.subscribe.domain.params.UpdateSubscribeLogStatusParams;
import com.aio.runtime.subscribe.service.SubscribeLogService;
import cn.aio1024.framework.basic.domain.amis.AmisResult;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lzm
 * @desc Mapping日志控制器
 * @date 2024/05/13
 */
@RestController
@Slf4j
@RequestMapping("/runtime/aio/log/subscribe/")
public class SubscribeLogController {
    @Autowired
    private SubscribeLogService subscribeLogService;
    @GetMapping("page")
    public AmisResult getRecordPage(@ModelAttribute QuerySubscribeLogParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = subscribeLogService.getPage(params,page);
        return AmisResult.success(pageResult);
    }
    @PostMapping("handled")
    public AmisResult updateSubscribeToHandled(@RequestBody UpdateSubscribeLogStatusParams params){
        subscribeLogService.updateSubscribeLogStatusToHandled(params);
        return AmisResult.success("标记日志为已处理");
    }
}
