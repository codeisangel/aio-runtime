package com.guodun.aio.document.user.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.guodun.aio.document.user.domain.params.LoginParams;
import com.kgo.flow.common.domain.amis.AmisResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lizhenming
 * @desc:
 * @date 2022/12/9 22:49
 */
@RestController
@Slf4j
@RequestMapping("/kgo/login")
public class LoginController {
    @PostMapping( )
    public AmisResult login(@RequestBody LoginParams loginParams){
        log.info("登录请求入参 ： {} ", JSON.toJSONString(loginParams));
        AmisResult success = AmisResult.success();
        success.setToken(IdUtil.fastSimpleUUID());
        return success;

    }
}
