package com.aio.runtime.security.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aio.runtime.security.domain.LoginParams;
import com.aio.runtime.security.domain.RuntimeSecurityProperties;
import com.aio.runtime.security.domain.UserInfoResult;
import com.kgo.aio.framework.basic.spring.utils.ServletUtils;
import com.kgo.flow.common.domain.amis.AmisResult;
import com.kgo.framework.basic.adapter.user.AioSecurityAdapter;
import com.kgo.framework.basic.integration.user.domain.AioUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lizhenming
 * @desc:
 * @date 2022/12/9 22:49
 */
@RestController
@Slf4j
public class RuntimeLoginController {
    @Autowired
    private AioSecurityAdapter securityAdapter;

    @Autowired
    private RuntimeSecurityProperties runtimeSecurityProperties;
    @PostMapping("/security/person/login")
    public AmisResult login(@RequestBody LoginParams loginParams){

        if (!StringUtils.equals(loginParams.getUsername(), runtimeSecurityProperties.getUsername())){
            log.error("用户名[ {} ]不是系统配置的用户[ {} ] ",loginParams.getUsername(),runtimeSecurityProperties.getUsername());
            return AmisResult.fail(50014,"账号或密码错误");
        }
        if (!StringUtils.equals(loginParams.getPassword(), runtimeSecurityProperties.getPassword())){
            log.error("用户的密码[ {} ] 不正确。",loginParams.getPassword());
            return AmisResult.fail(50014,"账号或密码错误");
        }
        AioUser user = new AioUser();
        user.setUserName(loginParams.getUsername());
        String token = securityAdapter.createToken(user);
        AmisResult success = AmisResult.success();
        success.setToken(token);
        return success;

    }
    @GetMapping("/security/user/login/info")
    public AmisResult getLoginInfo(@RequestParam String token){
        AioUser tokenInfo = securityAdapter.getTokenInfo(token);
        if (ObjectUtil.isNull(tokenInfo)){
            return AmisResult.fail(40100,"未获取到用户");
        }
        UserInfoResult userInfoResult = new UserInfoResult();
        userInfoResult.setName(tokenInfo.getUserName());
        AmisResult success = AmisResult.success(userInfoResult);
        return success;
    }

    @PostMapping("/security/logout")
    public void logout() {
        HttpServletRequest request = ServletUtils.getRequest();
        String token = request.getHeader("Token");
        if (StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }
        securityAdapter.removeToken(token);


    }
}
