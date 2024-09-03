package com.guodun.aio.document.user.controller;

import com.aio.runtime.security.domain.UserInfoResult;
import cn.aio1024.framework.basic.domain.amis.AmisResult;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author lizhenming
 * @desc:
 * @date 2022/12/9 22:49
 */
@RestController
@Slf4j
@RequestMapping("/kgo/user")
public class UserController {
    @GetMapping()
    public AmisResult getUser(){
        UserInfoResult userInfoResult = new UserInfoResult();
        userInfoResult.setName("李振明");
        userInfoResult.setRoles(Arrays.asList("admin"));
        return AmisResult.success(userInfoResult);
    }
    @GetMapping("id")
    public AmisResult getUser2(@RequestParam String userId){
        if (StringUtils.isNotBlank(userId)){
            throw new RuntimeException("这是一个测试异常的请求");
        }
        UserInfoResult userInfoResult = new UserInfoResult();
        userInfoResult.setName("李振明");
        userInfoResult.setRoles(Arrays.asList("admin"));
        return AmisResult.success(userInfoResult);

    }
}
