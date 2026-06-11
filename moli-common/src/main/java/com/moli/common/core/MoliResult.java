package com.moli.common.core;


import com.moli.common.enums.ResponseCodeEnums;
import lombok.Data;

import java.io.Serializable;

@Data
public class MoliResult<T> implements Serializable {

    private static final long serialVersionUID = -875864042638324305L;

    private static final int SUCCESS_CODE = ResponseCodeEnums.SUCCESS_CODE.getCode();

    private T data;
    private int code;
    private String msg;

    public MoliResult(T data) {
        this.setData(data);
    }

    public MoliResult(T data, int code) {
        this.setData(data);
        this.code = code;
    }

    public MoliResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public MoliResult(T data, int code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public MoliResult() {
    }

    public static <T> MoliResult<T> success() {
        MoliResult<T> result = new MoliResult<>();
        result.setCode(SUCCESS_CODE);
        return result;
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
        return new MoliResult(ResponseCodeEnums.ERROR.getCode());
    }

    public static <T> MoliResult<T> error(T t) {
        return new MoliResult(t, ResponseCodeEnums.ERROR.getCode());
    }

    public static <T> MoliResult<T> error(T data, String message) {
        MoliResult<T> moliResult = new MoliResult<>();
        moliResult.setCode(ResponseCodeEnums.ERROR.getCode());
        moliResult.setData(data);
        moliResult.setMsg(message);
        return moliResult;
    }

    public static <T> MoliResult<T> error(int code, T data, String message) {
        MoliResult<T> moliResult = new MoliResult<>();
        moliResult.setCode(code);
        moliResult.setData(data);
        moliResult.setMsg(message);
        return moliResult;
    }

    public static <T> MoliResult<T> errorMsg(int code, String message) {
        return new MoliResult(code, message);
    }

}


