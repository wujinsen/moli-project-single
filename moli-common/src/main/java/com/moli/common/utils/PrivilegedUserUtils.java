package com.moli.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moli.common.constant.CommonConstant;
import com.moli.common.domain.entity.SysUser;

/**
 * 超级管理员（superadmin）与特殊管理员（admin）的可见性与访问控制。
 */
public final class PrivilegedUserUtils {

    private PrivilegedUserUtils() {
    }

    /** 是否为需对外隐藏的特殊账号（superadmin / admin） */
    public static boolean isPrivilegedAccount(String userName) {
        return CommonConstant.isSuperAdmin(userName);
    }

    /** 是否为拥有最大权限的超级管理员 */
    public static boolean isSuperAdminAccount(String userName) {
        return CommonConstant.SUPER_ADMIN.equals(userName);
    }

    /** 是否拥有全部接口权限、系统准入与菜单 */
    public static boolean hasFullPermission(String userName) {
        return CommonConstant.hasFullPermission(userName);
    }

    /**
     * 用户列表查询：非特殊账号登录时，隐藏 superadmin 与 admin。
     */
    public static void applyListVisibilityFilter(LambdaQueryWrapper<SysUser> wrapper, String currentUserName) {
        if (!isPrivilegedAccount(currentUserName)) {
            wrapper.and(w -> w.ne(SysUser::getUserName, CommonConstant.SUPER_ADMIN)
                    .ne(SysUser::getUserName, CommonConstant.LEGACY_SUPER_ADMIN));
        }
    }

    /**
     * 当前用户是否可查看目标用户（特殊账号仅特殊账号可见）。
     */
    public static boolean canViewUser(SysUser target, SysUser current) {
        if (target == null) {
            return false;
        }
        if (!isPrivilegedAccount(target.getUserName())) {
            return true;
        }
        return current != null && isPrivilegedAccount(current.getUserName());
    }

}
