package com.aio.runtime.record.log.controller;

import com.aio.runtime.record.log.domain.QueryRecordParams;
import com.aio.runtime.record.log.service.MappingLogService;
import cn.aio1024.framework.basic.domain.amis.AmisResult;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
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
@RequestMapping("/runtime/aio/log/mapping/record")
public class MappingLogController {
    @Autowired
    private MappingLogService mappingLogService;
    @GetMapping("page")
    public AmisResult getRecordPage(@ModelAttribute QueryRecordParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = mappingLogService.searchRecords(params,page);
        return AmisResult.success(pageResult);
    }
    @GetMapping("info")
    public AmisResult getRecordInfo(){
        return AmisResult.success(mappingLogService.getRecordInfo());
    }



}
