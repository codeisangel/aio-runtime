package com.guodun.aio.document.user.domain.result;

import lombok.Data;

import java.util.List;

/**
 * @author lizhenming
 * @desc:
 * @date 2022/12/9 23:20
 */
@Data
public class UserInfoResult {
    private String introduction;
    private String avatar = "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif";
    private String name;
    private List<String> roles;
}
