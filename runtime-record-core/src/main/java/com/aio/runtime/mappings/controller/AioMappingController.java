package com.aio.runtime.mappings.controller;

import com.aio.runtime.mappings.domain.QueryMappingParams;
import com.aio.runtime.mappings.service.IAioMappingService;
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
 * @desc 接口控制器
 * @date 2024/07/27
 */
@RestController
@Slf4j
@RequestMapping("/runtime/aio/mapping")
public class AioMappingController {
    @Autowired
    private IAioMappingService aioMappingService;
    @GetMapping("page")
    public AmisResult getEnvironmentItemPage(@ModelAttribute QueryMappingParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = aioMappingService.getPage(params,page);
        return AmisResult.success(pageResult);
    }
}
