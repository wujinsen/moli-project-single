package com.moli.common.constant;

public class RedisConstant {
    /**
     * TOKEN前缀
     */
    public static String REDIS_PREFIX_LOGIN = "login_token_%s";
    /**
     * 过期时间2小时
     */
    public static Integer REDIS_EXPIRE_TWO = 7200;
    /**
     * 过期时间15分
     */
    public static Integer REDIS_EXPIRE_EMAIL = 900;
    /**
     * 过期时间5分钟
     */
    public static Integer REDIS_EXPIRE_KAPTCHA = 300;
    /**
     * 暂无过期时间
     */
    public static Integer REDIS_EXPIRE_NULL = -1;

}
