package com.aio.runtime.reported.controller;

import cn.aio1024.framework.basic.domain.amis.AmisResult;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import com.aio.runtime.reported.domain.params.AddErrorParams;
import com.aio.runtime.reported.domain.params.QueryErrorParams;
import com.aio.runtime.reported.service.ErrorReportedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lzm
 * @desc 错误上报接口
 * @date 2024/10/17
 */
@RestController
@Slf4j
@RequestMapping("/runtime/aio/error/reported")
public class ErrorReportedController {
    @Autowired
    private ErrorReportedService errorReportedService;
    @PostMapping()
    public AmisResult addErrorReported(@RequestBody AddErrorParams params){
        errorReportedService.addError(params);
        return AmisResult.success();
    }
    @PostMapping("page")
    public AmisResult getErrorReportedPage(@RequestBody QueryErrorParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = errorReportedService.getPage(params,page);
        return AmisResult.success(pageResult);
    }
}
