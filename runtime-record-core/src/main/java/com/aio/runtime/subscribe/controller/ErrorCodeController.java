package com.aio.runtime.subscribe.controller;

import cn.aio1024.framework.basic.domain.amis.AmisResult;
import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import com.aio.runtime.subscribe.domain.bo.ErrorCodeBo;
import com.aio.runtime.subscribe.domain.params.AddErrorCodeParams;
import com.aio.runtime.subscribe.domain.params.QueryErrorCodeParams;
import com.aio.runtime.subscribe.service.ErrorCodeService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @author lzm
 * @desc Mapping日志控制器
 * @date 2024/05/13
 */
@RestController
@Slf4j
@RequestMapping("/runtime/aio/error/code")
public class ErrorCodeController {
    @Autowired
    private ErrorCodeService errorCodeService;
    @GetMapping("page")
    public AmisResult getRecordPage(@ModelAttribute QueryErrorCodeParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = errorCodeService.getPage(params,page);
        return AmisResult.success(pageResult);
    }

    @PostMapping()
    public AmisResult addErrorCode(@RequestBody AddErrorCodeParams params){
       errorCodeService.addErrorCode(params);
        return AmisResult.success();
    }

    @PutMapping()
    public AmisResult updateErrorCode(@RequestBody ErrorCodeBo params){
        errorCodeService.updateErrorCode(params);
        return AmisResult.success();
    }

    @DeleteMapping()
    public AmisResult deleteErrorCode(@RequestBody ErrorCodeBo params){
        Assert.hasText(params.getId(),"错误码不能为空");
        errorCodeService.deleteErrorCode(params.getId());
        return AmisResult.success();
    }

    @GetMapping("export")
    public void exportErrorCode(@ModelAttribute QueryErrorCodeParams params, HttpServletResponse response) {
        KgoPage page = new KgoPage();
        page.setPageSize(5000);
        PageResult pageResult = errorCodeService.getPage(params,page);
        log.info("错误码列表总数： [ {} ] 条", pageResult.getTotal());
        try {
            response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode("errorCodeList", "UTF-8"));
            byte[] file = JSON.toJSONBytes(pageResult.getList());
            OutputStream os = response.getOutputStream();
            os.write(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
