package com.aio.runtime.beans.controller;

import com.aio.runtime.beans.domain.QueryBeanParams;
import com.aio.runtime.beans.service.IAioBeansService;
import com.kgo.flow.common.domain.amis.AmisResult;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import com.kgo.framework.basic.adapter.user.annotations.AioSecurityVerify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzm
 * @desc bean查询接口
 * @date 2024/08/06
 */
@RestController
@Slf4j
@RequestMapping("/runtime/aio/bean")
public class IAioBeansController {
    @Autowired
    private IAioBeansService beansService;
    @GetMapping("page")
    @AioSecurityVerify
    public AmisResult getEnvironmentItemPage(@ModelAttribute QueryBeanParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = beansService.getPage(params,page);
        return AmisResult.success(pageResult);
    }
}
