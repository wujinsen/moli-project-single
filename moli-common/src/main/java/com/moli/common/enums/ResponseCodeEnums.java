package com.moli.common.enums;

public enum ResponseCodeEnums {

    SUCCESS_CODE(200, "成功", "成功"),
    NO_DATA_CODE(201, "返回数据为空", "成功，无数据"),
    UNAUTHORIZED(401, "认证失败，无法访问系统资源", "认证失败，无法访问系统资源"),
    ERROR(500, "系统内部错误", "系统内部错误"),
    LOG_ERROR(5001, "日志记录错误", "日志记录错误"),
    ASPECT_ERROR(5002, "切面运行错误", "日志记录错误"),
    BEAN_OPT_ERROR(5003, "java bean 操作错误", "java bean 操作错误"),
    UPDATE_PW_ERROR(5004, "密码已过期,为了账号安全,请修改密码", "密码已过期,为了账号安全,请修改密码"),
    REPEAT_PW_ERROR(5005, "重复密码", "重复密码"),
    PARAMS_TYPE_ERROR_CODE(10000, "参数类型不匹配", "参数类型不匹配"),
    API_ERROR_CODE(10001, "接口调用错误", "接口调用错误"),
    SYSTEM_ERROR_CODE(10002, "系统错误", "系统错误"),
    PARAMS_EMPTY_ERROR_CODE(10003, "参数为空", "参数为空"),
    PARAMS_ERROR_CODE(10004, "参数验证错误", "参数验证错误"),
    SERVICE_ERROR_CODE(10005, "服务异常", "服务异常"),
    TOKEN_INVALID_ERROR_CODE(10006, "请登录", "token值无效"),
    TIMESTAMP_ERROR_CODE(10007, "时间戳错误", "时间戳错误"),
    JSON_PARAM_ERROR_CODE(10008, "参数JSON解析错误", "参数JSON解析错误"),
    AUTHOR_ERROR_CODE(402, "无操作权限", "无操作权限"),
    VALIDATE_ERROR_CODE(10010, "规则校验不通过", "规则校验不通过"),
    REPEAT_ERROR_CODE(10011, "请勿重复提交", "重复提交"),
    BIZ_ERROR_CODE(10012, "业务异常", "业务逻辑错误");

    private Integer code;
    private String message;
    private String desc;

    private ResponseCodeEnums(Integer code, String message, String desc) {
        this.code = code;
        this.message = message;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
