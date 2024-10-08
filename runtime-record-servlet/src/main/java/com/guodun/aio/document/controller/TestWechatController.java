package com.guodun.aio.document.controller;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author lzm
 * @desc 测试微信登录
 * @date 2024/09/19
 */
@RestController
@Slf4j
@RequestMapping("/test/wechat")
public class TestWechatController {
    @RequestMapping("/login")
    public RedirectView login(@RequestParam String code, @RequestParam String state){
        String url = StrUtil.format("http://192.168.2.21:9001/security/authorize/wechat/official/login/callback?code={}&state={}", code,state);
        return new RedirectView(url);
    }
}
