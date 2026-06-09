package com.moli.common.constant;

/**
 * 与 sys_menu.perms 保持一致
 */
public final class PermissionConstants {

    private PermissionConstants() {
    }

    public static final String SUPER_ADMIN = "*:*:*";

    public static final String SYSTEM_USER_LIST = "system:user:list";
    public static final String SYSTEM_ROLE_LIST = "system:role:list";
    public static final String SYSTEM_MENU_LIST = "system:menu:list";
    public static final String SYSTEM_DEPT_LIST = "system:dept:list";
    public static final String SYSTEM_POST_LIST = "system:post:list";
    public static final String SYSTEM_DICT_LIST = "system:dict:list";
    public static final String SYSTEM_OPERLOG_LIST = "system:operlog:list";
    public static final String SYSTEM_LOGINLOG_LIST = "system:loginlog:list";

    public static final String OPERATION_PROJECT_LIST = "operation:project:list";
    public static final String OPERATION_SERVER_LIST = "operation:server:list";
    public static final String OPERATION_PLATFORM_LIST = "operation:platform:list";
    public static final String OPERATION_COMPONENT_LIST = "operation:component:list";

    public static final String ROLE_ASSIGN_REFRESH_MSG = "角色授权已更新，请通知相关用户刷新页面后查看新菜单";
}
