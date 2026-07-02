package com.moli.common.exception;


import com.moli.common.core.MoliResult;
import com.moli.common.enums.ResponseCodeEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理 如果使用@RestControllerAdvice注解,则会将返回的数据类型转换成JSON格式
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 自定义异常
     */
    @ExceptionHandler(value = BaseException.class)
    public MoliResult<Object> baseExceptionHandler(HttpServletRequest req, BaseException e) {
        log.error("GlobalExceptionHandler baseExceptionHandler error: {}", req.getRequestURI(), e);
        if (e.getErrorCode() != null) {
            return MoliResult.errorMsg(e.getErrorCode(), e.getErrorMsg());
        }
        return MoliResult.errorMsg(ResponseCodeEnums.BIZ_ERROR_CODE.getCode(), e.getErrorMsg());
    }

    /**
     * Shiro 权限不足
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public MoliResult<Object> handlerAuthorizationException(AuthorizationException e) {
        log.warn("GlobalExceptionHandler authorization denied: {}", e.getMessage());
        return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限操作");
    }

    /**
     * 处理空指针的异常
     */
    @ExceptionHandler(value = NullPointerException.class)
    public MoliResult<Object> handlerNullPointerException(HttpServletRequest req, NullPointerException e) {
        log.error("GlobalExceptionHandler handlerNullPointerException error: {}", req.getRequestURI(), e);
        return MoliResult.error("空指针异常:", e.getMessage());
    }

    /**
     * 数据库错误异常
     */
    @ExceptionHandler(value = DataAccessException.class)
    public MoliResult<Object> handlerDataAccessException(HttpServletRequest req, DataAccessException e) {
        log.error("GlobalExceptionHandler handlerDataAccessException error: {} {}", req.getRequestURI(), e.getMessage(), e);
        return MoliResult.error("数据库错误", e.getMessage());
    }

    /**
     * 字段长度等数据库约束异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public MoliResult<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("GlobalExceptionHandler handleDataIntegrityViolationException error: {}", e.getMessage(), e);
        return MoliResult.error("字段太长,超出数据库字段的长度");
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(value = Exception.class)
    public MoliResult<Object> handlerException(HttpServletRequest req, Exception e) {
        log.error("GlobalExceptionHandler handlerException error: {} {}", req.getRequestURI(), e.getMessage(), e);
        return MoliResult.error("服务器错误", e.getMessage());
    }

    /**
     * 数字格式化异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public MoliResult<Object> handlerNumberFormatException(HttpServletRequest req, NumberFormatException e) {
        log.error("GlobalExceptionHandler handlerNumberFormatException error: {}", req.getRequestURI(), e);
        return MoliResult.error("数字格式化异常", e.getMessage());
    }

    /**
     * 参数拦截异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public MoliResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("GlobalExceptionHandler handleMethodArgumentNotValidException error: {}", e.getMessage(), e);
        return MoliResult.error(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(BindException.class)
    public MoliResult<Object> handlerBindException(BindException e) {
        log.error("GlobalExceptionHandler handlerBindException error: {}", e.getMessage(), e);
        return MoliResult.error(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 上传文件过大
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public MoliResult<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("GlobalExceptionHandler handleMaxUploadSizeExceededException error: {}", e.getMessage(), e);
        return MoliResult.error("文件大小超出限制, 请压缩或降低文件质量");
    }
}
