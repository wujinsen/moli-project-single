package com.moli.config.shiro;

import com.moli.common.core.MoliResult;
import com.moli.common.enums.ResponseCodeEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ShiroExceptionHandler {

    @ExceptionHandler({UnauthorizedException.class, AuthorizationException.class})
    public MoliResult<Boolean> handleUnauthorized(AuthorizationException e) {
        log.warn("permission denied: {}", e.getMessage());
        return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限操作");
    }
}
