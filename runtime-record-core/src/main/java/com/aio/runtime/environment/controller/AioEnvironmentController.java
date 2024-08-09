package com.aio.runtime.environment.controller;

import com.aio.runtime.environment.domain.QueryEnvironmentParams;
import com.aio.runtime.environment.service.IAioEnvironmentService;
import com.kgo.flow.common.domain.amis.AmisResult;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzm
 * @desc 环境信息查询接口
 * @date 2024/07/26
 */
@RestController
@Slf4j
@RequestMapping("/runtime/aio/environment")
@ConditionalOnClass(EnvironmentEndpoint.class)
public class AioEnvironmentController {
    @Autowired
    private IAioEnvironmentService iAioEnvironmentService;
    @GetMapping("page")
    public AmisResult getEnvironmentItemPage(@ModelAttribute QueryEnvironmentParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = iAioEnvironmentService.getPage(params,page);
        return AmisResult.success(pageResult);
    }
}
