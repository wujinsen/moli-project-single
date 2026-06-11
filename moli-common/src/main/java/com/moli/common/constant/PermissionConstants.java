package com.moli.common.constant;

/**
 * 与 sys_menu.perms 保持一致
 */
public final class PermissionConstants {

    private PermissionConstants() {
    }

    public static final String SUPER_ADMIN = "*:*:*";

    public static final String SYSTEM_USER_LIST = "system:user:list";
    public static final String SYSTEM_USER_ADD = "system:user:add";
    public static final String SYSTEM_USER_EDIT = "system:user:edit";
    public static final String SYSTEM_USER_REMOVE = "system:user:remove";
    public static final String SYSTEM_USER_RESET_PWD = "system:user:resetPwd";
    public static final String SYSTEM_USER_ASSIGN_ROLE = "system:user:assignRole";
    public static final String SYSTEM_USER_ASSIGN_SYSTEM = "system:user:assignSystem";
    public static final String SYSTEM_ROLE_LIST = "system:role:list";
    public static final String SYSTEM_ROLE_ADD = "system:role:add";
    public static final String SYSTEM_ROLE_EDIT = "system:role:edit";
    public static final String SYSTEM_ROLE_REMOVE = "system:role:remove";
    public static final String SYSTEM_MENU_LIST = "system:menu:list";
    public static final String SYSTEM_MENU_ADD = "system:menu:add";
    public static final String SYSTEM_MENU_EDIT = "system:menu:edit";
    public static final String SYSTEM_MENU_REMOVE = "system:menu:remove";
    public static final String SYSTEM_DEPT_LIST = "system:dept:list";
    public static final String SYSTEM_DEPT_ADD = "system:dept:add";
    public static final String SYSTEM_DEPT_EDIT = "system:dept:edit";
    public static final String SYSTEM_DEPT_REMOVE = "system:dept:remove";
    public static final String SYSTEM_POST_LIST = "system:post:list";
    public static final String SYSTEM_POST_ADD = "system:post:add";
    public static final String SYSTEM_POST_EDIT = "system:post:edit";
    public static final String SYSTEM_POST_REMOVE = "system:post:remove";
    public static final String SYSTEM_DICT_LIST = "system:dict:list";
    public static final String SYSTEM_DICT_ADD = "system:dict:add";
    public static final String SYSTEM_DICT_EDIT = "system:dict:edit";
    public static final String SYSTEM_DICT_REMOVE = "system:dict:remove";
    public static final String SYSTEM_OPERLOG_LIST = "system:operlog:list";
    public static final String SYSTEM_OPERLOG_REMOVE = "system:operlog:remove";
    public static final String SYSTEM_LOGINLOG_LIST = "system:loginlog:list";
    public static final String SYSTEM_LOGINLOG_REMOVE = "system:loginlog:remove";
    /** 业务系统注册（sys_system 维护） */
    public static final String SYSTEM_SYSTEM_LIST = "system:system:list";
    public static final String SYSTEM_SYSTEM_ADD = "system:system:add";
    public static final String SYSTEM_SYSTEM_EDIT = "system:system:edit";
    public static final String SYSTEM_SYSTEM_REMOVE = "system:system:remove";

    public static final String OPERATION_PROJECT_LIST = "operation:project:list";
    public static final String OPERATION_PROJECT_ADD = "operation:project:add";
    public static final String OPERATION_PROJECT_EDIT = "operation:project:edit";
    public static final String OPERATION_PROJECT_REMOVE = "operation:project:remove";
    public static final String OPERATION_SERVER_LIST = "operation:server:list";
    public static final String OPERATION_SERVER_ADD = "operation:server:add";
    public static final String OPERATION_SERVER_EDIT = "operation:server:edit";
    public static final String OPERATION_SERVER_REMOVE = "operation:server:remove";
    public static final String OPERATION_PLATFORM_LIST = "operation:platform:list";
    public static final String OPERATION_PLATFORM_ADD = "operation:platform:add";
    public static final String OPERATION_PLATFORM_EDIT = "operation:platform:edit";
    public static final String OPERATION_PLATFORM_REMOVE = "operation:platform:remove";
    public static final String OPERATION_COMPONENT_LIST = "operation:component:list";
    public static final String OPERATION_COMPONENT_ADD = "operation:component:add";
    public static final String OPERATION_COMPONENT_EDIT = "operation:component:edit";
    public static final String OPERATION_COMPONENT_REMOVE = "operation:component:remove";

    public static final String ROLE_ASSIGN_REFRESH_MSG = "角色授权已更新，请通知相关用户刷新页面后查看新菜单";
}
