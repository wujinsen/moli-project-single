package com.moli.common.constant;


public class CommonConstant {

    public static final Integer IS_DELETE = 1;

    public static final Integer UN_DELETE = 0;

    public static final Integer YES = 1;

    public static final Integer NO = 0;

    public static final String  BUCKET_PICTURE_NAME = "picture";

    public static final String  BUCKET_FILE_NAME = "file";

    public static final String  BUCKET_VIDEO_NAME = "video";

    /** Layout组件标识 */
    public final static String LAYOUT = "Layout";

    /** ParentView组件标识 */
    public final static String PARENT_VIEW = "ParentView";

    /** InnerLink组件标识 */
    public final static String INNER_LINK = "InnerLink";

    /** 菜单类型（目录） */
    public static final String TYPE_DIR = "M";

    /** 菜单类型（菜单） */
    public static final String TYPE_MENU = "C";

    /** 菜单类型（按钮） */
    public static final String TYPE_BUTTON = "F";

    public static final String NO_REDIRECT = "noRedirect";

    /** 超级管理员：拥有系统最大权限 */
    public static final String SUPER_ADMIN = "superadmin";

    /** 特殊管理员账号：对外隐藏，仅特殊账号互相可见 */
    public static final String LEGACY_SUPER_ADMIN = "admin";

    /** superadmin 与 admin 均属于需保护的特殊账号 */
    public static boolean isSuperAdmin(String userName) {
        return SUPER_ADMIN.equals(userName) || LEGACY_SUPER_ADMIN.equals(userName);
    }

    /**
     * 是否拥有全部接口权限（*:*:*）及全部系统准入、全部菜单。
     */
    public static boolean hasFullPermission(String userName) {
        return isSuperAdmin(userName);
    }

}
