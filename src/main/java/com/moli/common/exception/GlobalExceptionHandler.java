package com.moli.common.exception;


import com.moli.common.core.MoliResult;
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
        log.error("数据异常！原因是:", e);
        log.error("接口名URL: {}" + req.getRequestURI());
        return MoliResult.success(e.getErrorMsg());
    }

    /**
     * 处理空指针的异常
     */
    @ExceptionHandler(value = NullPointerException.class)
    public MoliResult<Object> handlerNullPointerException(HttpServletRequest req, NullPointerException e) {
        log.error("空指针异常！原因是:", e);
        log.error("接口名URL: " + req.getRequestURI());
        return MoliResult.error("空指针异常", e.getMessage());
    }

    /**
     * 数据库错误异常
     */
    @ExceptionHandler(value = DataAccessException.class)
    public MoliResult<Object> handlerDataAccessException(HttpServletRequest req, DataAccessException e) {
        log.error("数据库错误！原因是:", e);
        log.error("接口名URL: " + req.getRequestURI());
        return MoliResult.error("数据库错误", e.getMessage());
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(value = Exception.class)
    public MoliResult<Object> handlerException(HttpServletRequest req, Exception e) {
        log.error("服务器错误！原因是:", e);
        log.error("接口名URL: " + req.getRequestURI());
        return MoliResult.error("服务器错误", e.getMessage());
    }

    /**
     * 数字格式化异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public MoliResult<Object> handlerNumberFormatException(HttpServletRequest req, NumberFormatException e) {
        log.error("数字格式化异常！原因是:", e);
        log.error("接口名URL: " + req.getRequestURI());
        return MoliResult.error("数字格式化异常", e.getMessage());
    }

    /**
     * 参数拦截异常
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public MoliResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return MoliResult.error(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    // validation 报错捕捉
    @ExceptionHandler(BindException.class)
    public MoliResult<Object> HandlerBindException(BindException e) {

        return MoliResult.error(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 处理Shiro权限拦截异常
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public MoliResult<Object> handlerAuthorizationException() {
        return MoliResult.error("权限不足");
    }

    /**
     * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public MoliResult<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        return MoliResult.error("文件大小超出10MB限制, 请压缩或降低文件质量! ");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public MoliResult<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return MoliResult.error("字段太长,超出数据库字段的长度");
    }

}
