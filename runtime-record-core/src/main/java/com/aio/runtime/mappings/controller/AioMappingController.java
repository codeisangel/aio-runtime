package com.aio.runtime.mappings.controller;

import cn.aio1024.framework.basic.domain.amis.AmisResult;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import com.aio.runtime.mappings.domain.QueryMappingParams;
import com.aio.runtime.mappings.service.IAioMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
@ConditionalOnClass(MappingsEndpoint.class)
public class AioMappingController {
    @Autowired(required = false)
    private IAioMappingService aioMappingService;
    @GetMapping("page")
    public AmisResult getEnvironmentItemPage(@ModelAttribute QueryMappingParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = aioMappingService.getPage(params,page);
        return AmisResult.success(pageResult);
    }
}
