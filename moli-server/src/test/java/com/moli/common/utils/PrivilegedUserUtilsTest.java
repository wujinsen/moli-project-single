package com.moli.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moli.common.constant.CommonConstant;
import com.moli.common.domain.entity.SysUser;
import com.moli.testsupport.MybatisPlusTestSupport;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PrivilegedUserUtilsTest {

    @BeforeClass
    public static void initMybatisPlusMetadata() {
        MybatisPlusTestSupport.initAll();
    }

    @Test
    public void isPrivilegedAccount_superadminAndAdmin() {
        Assert.assertTrue(PrivilegedUserUtils.isPrivilegedAccount(CommonConstant.SUPER_ADMIN));
        Assert.assertTrue(PrivilegedUserUtils.isPrivilegedAccount(CommonConstant.LEGACY_SUPER_ADMIN));
        Assert.assertFalse(PrivilegedUserUtils.isPrivilegedAccount("operator"));
    }

    @Test
    public void isSuperAdminAccount_onlySuperadmin() {
        Assert.assertTrue(PrivilegedUserUtils.isSuperAdminAccount(CommonConstant.SUPER_ADMIN));
        Assert.assertFalse(PrivilegedUserUtils.isSuperAdminAccount(CommonConstant.LEGACY_SUPER_ADMIN));
        Assert.assertFalse(PrivilegedUserUtils.isSuperAdminAccount("operator"));
    }

    @Test
    public void canViewUser_normalUserCanViewNormalUser() {
        SysUser target = user("operator");
        SysUser current = user("manager");
        Assert.assertTrue(PrivilegedUserUtils.canViewUser(target, current));
    }

    @Test
    public void canViewUser_normalUserCannotViewSuperadmin() {
        SysUser target = user(CommonConstant.SUPER_ADMIN);
        SysUser current = user("operator");
        Assert.assertFalse(PrivilegedUserUtils.canViewUser(target, current));
    }

    @Test
    public void canViewUser_normalUserCannotViewLegacyAdmin() {
        SysUser target = user(CommonConstant.LEGACY_SUPER_ADMIN);
        SysUser current = user("operator");
        Assert.assertFalse(PrivilegedUserUtils.canViewUser(target, current));
    }

    @Test
    public void canViewUser_superadminCanViewSelf() {
        SysUser target = user(CommonConstant.SUPER_ADMIN);
        SysUser current = user(CommonConstant.SUPER_ADMIN);
        Assert.assertTrue(PrivilegedUserUtils.canViewUser(target, current));
    }

    @Test
    public void canViewUser_superadminCanViewLegacyAdmin() {
        SysUser target = user(CommonConstant.LEGACY_SUPER_ADMIN);
        SysUser current = user(CommonConstant.SUPER_ADMIN);
        Assert.assertTrue(PrivilegedUserUtils.canViewUser(target, current));
    }

    @Test
    public void applyListVisibilityFilter_hidesPrivilegedAccountsForNormalUser() {
        LambdaQueryWrapper<SysUser> operatorWrapper = new LambdaQueryWrapper<>();
        PrivilegedUserUtils.applyListVisibilityFilter(operatorWrapper, "operator");
        LambdaQueryWrapper<SysUser> privilegedWrapper = new LambdaQueryWrapper<>();
        PrivilegedUserUtils.applyListVisibilityFilter(privilegedWrapper, CommonConstant.SUPER_ADMIN);
        Assert.assertNotEquals(operatorWrapper.getSqlSegment(), privilegedWrapper.getSqlSegment());
    }

    @Test
    public void applyListVisibilityFilter_noExtraFilterForSuperadmin() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        PrivilegedUserUtils.applyListVisibilityFilter(wrapper, CommonConstant.SUPER_ADMIN);
        Assert.assertEquals("", wrapper.getCustomSqlSegment());
    }

    @Test
    public void applyListVisibilityFilter_noExtraFilterForLegacyAdmin() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        PrivilegedUserUtils.applyListVisibilityFilter(wrapper, CommonConstant.LEGACY_SUPER_ADMIN);
        Assert.assertEquals("", wrapper.getCustomSqlSegment());
    }

    private static SysUser user(String userName) {
        SysUser user = new SysUser();
        user.setUserName(userName);
        return user;
    }
}
