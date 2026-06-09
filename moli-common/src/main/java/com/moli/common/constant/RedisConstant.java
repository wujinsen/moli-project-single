package com.moli.common.constant;

public class RedisConstant {
    /**
     * TOKEN前缀
     */
    public static String REDIS_PREFIX_LOGIN = "login_token_%s";

    /**
     * Shiro 用户与 Session 映射（避免 SCAN 枚举，兼容 Serverless Redis）
     */
    public static String SHIRO_USER_SESSION_KEY = "moli:shiro:user-session:%s";
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

    /**
     * SSO 一次性 Ticket（%s = ticketId）
     */
    public static String SSO_TICKET_KEY = "moli:sso:ticket:%s";

    /**
     * SSO Ticket 默认有效期（秒）
     */
    public static Integer SSO_TICKET_EXPIRE = 60;

}
