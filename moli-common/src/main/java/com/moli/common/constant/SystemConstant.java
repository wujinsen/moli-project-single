package com.moli.common.constant;

/**
 * 业务系统常量
 */
public final class SystemConstant {

    private SystemConstant() {
    }

    /** 本项目（moli-admin）在 sys_system 中的编码 */
    public static final String DEFAULT_SYSTEM_CODE = "moli-admin";

    /** 同域/本集群：进入后直接加载菜单 */
    public static final String SSO_MODE_INTERNAL = "INTERNAL";

    /** 外部系统：Ticket 跳转 */
    public static final String SSO_MODE_EXTERNAL = "EXTERNAL";

    public static final Integer STATUS_ENABLED = 1;

    public static final Integer STATUS_DISABLED = 0;

}
