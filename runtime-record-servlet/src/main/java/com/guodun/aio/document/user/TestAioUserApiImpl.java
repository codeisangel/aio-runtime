package com.guodun.aio.document.user;

import cn.hutool.core.util.IdUtil;
import com.kgo.framework.basic.integration.user.AioUserApi;
import com.kgo.framework.basic.integration.user.domain.AioUser;
import org.springframework.stereotype.Component;

/**
 * @author lzm
 * @desc 测试集成用户权限
 * @date 2024/05/13
 */
@Component
public class TestAioUserApiImpl implements AioUserApi {
    @Override
    public AioUser getCurrentUser() {
        AioUser aioUser = new AioUser();
        aioUser.setUserId(String.valueOf(System.currentTimeMillis()));
        aioUser.setUserName("李振明");
        aioUser.setCompanyId(IdUtil.getSnowflakeNextIdStr());
        aioUser.setCompanyName("山东国盾网");
        return aioUser;
    }

    @Override
    public AioUser getUserById(String userId) {
        return null;
    }

    @Override
    public AioUser getUserByCardNo(String cardNo) {
        return null;
    }
}
