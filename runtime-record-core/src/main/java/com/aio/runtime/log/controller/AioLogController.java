package com.aio.runtime.log.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aio.runtime.log.domain.AioLogParamsBo;
import com.aio.runtime.log.domain.QueryLogParams;
import com.aio.runtime.log.service.AioLogQuerySchemeService;
import com.aio.runtime.log.service.AioLogService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kgo.flow.common.domain.amis.AmisOptions;
import com.kgo.flow.common.domain.amis.AmisResult;
import com.kgo.flow.common.domain.page.KgoPage;
import com.kgo.flow.common.domain.page.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lzm
 * @desc 日志查询
 * @date 2024/08/08
 */
@RestController
@Slf4j
@RequestMapping("/runtime/aio/log")
public class AioLogController {
    @Autowired
    private AioLogService logService;
    @Autowired
    private AioLogQuerySchemeService querySchemeService;


    @PostMapping("page")
    public AmisResult getEnvironmentItemPage(@RequestBody QueryLogParams params , @ModelAttribute KgoPage page){
        PageResult pageResult = logService.getPage(params,page);
        return AmisResult.success(pageResult);
    }

    @GetMapping("scheme/options")
    public AmisResult getQuerySchemeOptions(){
        AmisOptions options = new AmisOptions<>();
        List<AioLogParamsBo> list = querySchemeService.getList();
        if (ObjectUtil.isNull(list)){
            return AmisResult.success(options);
        }
        for (AioLogParamsBo aioLogParamsBo : list) {
            options.addOption(aioLogParamsBo.getSchemeName(), aioLogParamsBo.getId());
        }
        return AmisResult.success(options);
    }

    @GetMapping("scheme")
    public AmisResult getQueryScheme(@RequestParam String id){
        AioLogParamsBo paramsBo = querySchemeService.getById(id);
        if ( ObjectUtil.isNull(paramsBo)){
            return AmisResult.success(new Object());
        }
        Map<String,Object> result = new HashMap<>();
        JSONObject jsonObject = JSON.parseObject(paramsBo.getSchemeContent());
        result.put("scheme",jsonObject);
        result.put("schemeName",paramsBo.getSchemeName());
        result.put("id",paramsBo.getId());
        return AmisResult.success(result);
    }

    @PostMapping("scheme")
    public AmisResult getEnvironmentItemPage(@RequestBody AioLogParamsBo params ){
        querySchemeService.saveScheme(params);
        return AmisResult.success();
    }
}
