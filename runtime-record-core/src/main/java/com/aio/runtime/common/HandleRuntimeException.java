package com.aio.runtime.common;

import cn.aio1024.framework.basic.domain.amis.AmisResult;
import com.aio.runtime.domain.exception.RuntimeSecurityUnAuthException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @author lzm
 * @desc 处理运行时异常
 * @date 2024/09/02
 */
@ControllerAdvice
public class HandleRuntimeException {
    @ExceptionHandler(RuntimeSecurityUnAuthException.class)
    @ResponseBody
    public AmisResult handleExceptions(RuntimeSecurityUnAuthException ex, HttpServletResponse response){
        // 创建 JSON 响应体
       return AmisResult.fail(ex.getCode(), ex.getMessage());

    }

}
