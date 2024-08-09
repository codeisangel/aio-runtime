package com.aio.runtime.security.controller;

import com.aio.runtime.security.domain.LoginParams;
import com.aio.runtime.security.domain.UserInfoResult;
import com.kgo.flow.common.domain.amis.AmisResult;
import com.kgo.framework.basic.adapter.user.AioSecurityAdapter;
import com.kgo.framework.basic.integration.user.domain.AioUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lizhenming
 * @desc:
 * @date 2022/12/9 22:49
 */
@RestController
@Slf4j
@RequestMapping("/aio/security/")
public class RuntimeLoginController {
    @Autowired
    private AioSecurityAdapter securityAdapter;
    @PostMapping("runtime/login")
    public AmisResult login(@RequestBody LoginParams loginParams){

        if (!StringUtils.equals(loginParams.getUsername(), "admin")){
            return AmisResult.fail(50014,"账号或密码错误");
        }
        if (!StringUtils.equals(loginParams.getPassword(), "guodun@2024")){
            return AmisResult.fail(50014,"账号或密码错误");
        }
        AioUser user = new AioUser();
        user.setUserName("admin");
        String token = securityAdapter.createToken(user);
        AmisResult success = AmisResult.success();
        success.setToken(token);
        return success;

    }
    @GetMapping("runtime/login/info")
    public AmisResult getLoginInfo(@RequestParam String token){
        UserInfoResult userInfoResult = new UserInfoResult();
        userInfoResult.setName("admin");
        AmisResult success = AmisResult.success(userInfoResult);
        return success;

    }
}
