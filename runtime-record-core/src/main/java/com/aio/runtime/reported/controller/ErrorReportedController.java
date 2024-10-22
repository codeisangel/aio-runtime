package com.aio.runtime.reported.controller;

import cn.aio1024.framework.basic.domain.amis.AmisResult;
import com.aio.runtime.reported.domain.params.AddErrorParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzm
 * @desc 错误上报接口
 * @date 2024/10/17
 */
@RestController
@Slf4j
@RequestMapping("/runtime/aio/error/reported")
public class ErrorReportedController {
    @PostMapping()
    public AmisResult addErrorReported(@RequestBody AddErrorParams params){
        log.info("错误上报 ： {} ",params);
        return AmisResult.success();
    }
}
