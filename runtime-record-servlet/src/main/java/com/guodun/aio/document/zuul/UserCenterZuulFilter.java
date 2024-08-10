package com.guodun.aio.document.zuul;

import com.guodun.security.client.http.config.HttpClientProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lizhenming
 * @desc:
 * @date 2023/11/7 14:23
 */
//@Component
@Slf4j
public class UserCenterZuulFilter extends ZuulFilter {
    @Autowired
    private HttpClientProperties clientProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext rc = RequestContext.getCurrentContext();
        HttpServletRequest request = rc.getRequest();
        if (request.getRequestURI().indexOf("login") > 0) {
            String credential = clientProperties.getCredential();
            rc.addZuulRequestHeader("App_credential", credential);
        }
        String token = request.getHeader("token");
        if (StringUtils.isNotBlank(token)){
            rc.addZuulRequestHeader("token", token);
        }
        return null;
    }
}
