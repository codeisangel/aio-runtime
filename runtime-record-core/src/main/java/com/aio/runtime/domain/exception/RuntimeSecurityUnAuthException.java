package com.aio.runtime.domain.exception;

import lombok.Getter;

/**
 * @author lzm
 * @desc 运行时安全未授权异常
 * @date 2024/09/02
 */
@Getter
public class RuntimeSecurityUnAuthException extends RuntimeException{
    private Integer code = 50014;
    public RuntimeSecurityUnAuthException(){
        super("运行时接口授权失败");
    }
}
