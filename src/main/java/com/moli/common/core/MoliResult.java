package com.moli.common.core;


import com.moli.common.enums.ResponseCodeEnums;
import lombok.Data;

import java.io.Serializable;

@Data
public class MoliResult<T> implements Serializable {

    private static final long serialVersionUID = -875864042638324305L;

    private static final int SUCCESS_CODE = ResponseCodeEnums.SUCCESS_CODE.getCode();
    private static final int FAIL_CODE = -1;
    private static final int STATUS_404 = 404;
    private static final int STATUS_500 = ResponseCodeEnums.ERROR.getCode();
    private static final int STATUS_PROTECTED = 1000;


    private T data;
    private int code;
    private String message;

    public MoliResult(T data) {
        this.setData(data);
    }

    public MoliResult(T data, int code) {
        this.setData(data);
        this.code = code;
    }

    public MoliResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public MoliResult(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public MoliResult() {
    }

    public static <T> MoliResult<T> success() {
        return new MoliResult(SUCCESS_CODE);
    }


    public static <T> MoliResult<T> success(T t) {
        return new MoliResult(t, SUCCESS_CODE);
    }

    public static <T> MoliResult<T> success(T t, String message) {
        return new MoliResult(t, SUCCESS_CODE, message);
    }

    public static <T> MoliResult<T> success(T t, int code) {
        return new MoliResult(t, code);
    }

    public static <T> MoliResult<T> success(T t, int code, String message) {
        return new MoliResult(t, code, message);
    }

    public static <T> MoliResult<T> error() {
        return new MoliResult(SUCCESS_CODE);
    }


    public static <T> MoliResult<T> error(T t) {
        return new MoliResult(t, SUCCESS_CODE);
    }

    public static <T> MoliResult<T> error(T t, String message) {
        return new MoliResult(t, SUCCESS_CODE, message);
    }

    public static <T> MoliResult<T> error(T t, int code) {
        return new MoliResult(t, code);
    }

    public static <T> MoliResult<T> errorMsg(int code, String message) {
        return new MoliResult(code, message);
    }

    public static <T> MoliResult<T> error(T t, int code, String message) {
        return new MoliResult(t, code, message);
    }
}


