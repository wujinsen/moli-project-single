package com.moli.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.*;
import com.moli.common.domain.vo.*;
import com.moli.common.enums.ResponseCodeEnums;
import com.moli.common.page.PageRes;
import com.moli.common.utils.PrivilegedUserUtils;
import com.moli.config.util.SHA256Util;
import com.moli.config.util.ShiroUtils;
import com.moli.system.controller.UserController;
import com.moli.system.mapper.*;
import com.moli.system.service.SysSystemService;
import com.moli.system.service.UserRoleService;
import com.moli.system.service.UserService;
import com.moli.testsupport.AbstractApiTest;
import com.moli.testsupport.ControllerTestSupport;
import com.moli.testsupport.ShiroMockSupport;
import org.apache.shiro.SecurityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerApiTest extends AbstractApiTest {

    @InjectMocks
    private UserController controller;

    @Mock private SysUserMapper sysUserMapper;
    @Mock private SysUserRoleMapper sysUserRoleMapper;
    @Mock private RoleMapper roleMapper;
    @Mock private UserRoleService userRoleService;
    @Mock private UserService userService;
    @Mock private UserPostMapper userPostMapper;
    @Mock private PostMapper postMapper;
    @Mock private DeptMapper deptMapper;
    @Mock private SysSystemService sysSystemService;
    @Mock private SysSystemMapper sysSystemMapper;
    @Mock private SysUserSystemMapper sysUserSystemMapper;

    private SysUser operator() {
        SysUser u = new SysUser();
        u.setId(20L);
        u.setUserName("operator");
        u.setIsDelete(CommonConstant.UN_DELETE);
        return u;
    }

    private void allowView(SysUser user) {
        when(userService.canViewUser(user)).thenReturn(true);
    }

    private void applyOperatorVisibility() {
        doAnswer(inv -> {
            PrivilegedUserUtils.applyListVisibilityFilter(inv.getArgument(0), "operator");
            return null;
        }).when(userService).applyPrivilegedUserVisibility(any(LambdaQueryWrapper.class));
    }

    @Test
    public void GET_user_list() {
        PageRes<UserVo> page = new PageRes<>();
        page.setList(Collections.emptyList());
        when(userService.list(any(UserVo.class))).thenReturn(page);
        ControllerTestSupport.assertSuccess(controller.list(new UserVo()));
    }

    @Test
    public void POST_user_insert_requiresPassword() {
        UserVo vo = new UserVo();
        vo.setUserName("newuser");
        Assert.assertEquals((int) ResponseCodeEnums.ERROR.getCode(), controller.insert(vo).getCode());
    }

    @Test
    public void POST_user_insert_success() {
        UserVo vo = new UserVo();
        vo.setUserName("newuser");
        vo.setPassword("123456");
        ControllerTestSupport.assertSuccess(controller.insert(vo));
    }

    @Test
    public void PUT_user_update_selfProfile() {
        try (MockedStatic<SecurityUtils> shiro = ShiroMockSupport.mockUser("operator", 20L)) {
            SysUserVo req = new SysUserVo();
            req.setId(20L);
            req.setNickName("测试昵称");
            req.setEmail("test@example.com");
            ControllerTestSupport.stubUpdate(sysUserMapper);
            ControllerTestSupport.assertSuccess(controller.update(req));
        }
    }

    @Test
    public void GET_user_id() {
        SysUser user = operator();
        when(sysUserMapper.selectById(20L)).thenReturn(user);
        allowView(user);
        ControllerTestSupport.assertSuccess(controller.getInfo(20L));
    }

    @Test
    public void GET_user_getUserDetail() {
        SysUser user = operator();
        when(sysUserMapper.selectById(20L)).thenReturn(user);
        allowView(user);
        when(userPostMapper.selectList(any())).thenReturn(Collections.emptyList());
        ControllerTestSupport.assertSuccess(controller.getUserDetail(20L));
    }

    @Test
    public void GET_user_profile() {
        try (MockedStatic<SecurityUtils> shiro = ShiroMockSupport.mockUser("operator", 20L)) {
            SysUser user = operator();
            when(sysUserMapper.selectById(20L)).thenReturn(user);
            when(userPostMapper.selectList(any())).thenReturn(Collections.emptyList());
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(20L);
            userRole.setRoleId(2L);
            when(sysUserRoleMapper.selectList(any())).thenReturn(Collections.singletonList(userRole));
            SysRole role = new SysRole();
            role.setId(2L);
            role.setRoleName("系统管理员");
            when(roleMapper.selectList(any())).thenReturn(Collections.singletonList(role));
            ControllerTestSupport.assertSuccess(controller.getUserProfile());
        }
    }

    @Test
    public void PUT_user_language() {
        try (MockedStatic<SecurityUtils> shiro = ShiroMockSupport.mockUser("operator", 20L)) {
            SysUser req = new SysUser();
            req.setLanguage("zh-CN");
            ControllerTestSupport.stubUpdate(sysUserMapper);
            ControllerTestSupport.assertSuccess(controller.updateLanguage(req));
        }
    }

    @Test
    public void DELETE_user_userIds() {
        SysUser user = operator();
        when(sysUserMapper.selectById(20L)).thenReturn(user);
        allowView(user);
        ControllerTestSupport.stubUpdate(sysUserMapper);
        ControllerTestSupport.assertSuccess(controller.delete(new Long[]{20L}));
    }

    @Test
    public void PUT_user_changeStatus() {
        SysUser user = operator();
        when(sysUserMapper.selectById(20L)).thenReturn(user);
        allowView(user);
        SysUser req = new SysUser();
        req.setId(20L);
        req.setStatus(1);
        ControllerTestSupport.stubUpdate(sysUserMapper);
        ControllerTestSupport.assertSuccess(controller.changeStatus(req));
    }

    @Test
    public void GET_user_getRoleByUserId() {
        SysUser user = operator();
        when(sysUserMapper.selectById(20L)).thenReturn(user);
        allowView(user);
        when(sysUserRoleMapper.selectList(any())).thenReturn(Collections.emptyList());
        ControllerTestSupport.assertSuccess(controller.getRoleByUserId(20L));
    }

    @Test
    public void PUT_user_insertUserRole() {
        UserRoleVo vo = new UserRoleVo();
        vo.setUserId(20L);
        vo.setRoleIds(Collections.singletonList(1L));
        ControllerTestSupport.assertSuccess(controller.insertUserRole(vo));
    }

    @Test
    public void GET_user_getSystemByUserId() {
        SysUser user = operator();
        when(sysUserMapper.selectById(20L)).thenReturn(user);
        allowView(user);
        when(sysSystemService.listSystemIdsByUserId(20L)).thenReturn(Collections.singletonList(1L));
        when(sysSystemMapper.selectList(any())).thenReturn(Collections.emptyList());
        ControllerTestSupport.assertSuccess(controller.getSystemByUserId(20L));
    }

    @Test
    public void PUT_user_insertUserSystem() {
        SysUser user = operator();
        when(sysUserMapper.selectById(20L)).thenReturn(user);
        allowView(user);
        UserSystemVo vo = new UserSystemVo();
        vo.setUserId(20L);
        vo.setSystemIds(Collections.singletonList(1L));
        ControllerTestSupport.assertSuccess(controller.insertUserSystem(vo));
    }

    @Test
    public void GET_user_getUserBySystem() {
        UserVo req = new UserVo();
        req.setSystemId(1L);
        req.setPageNum(1);
        req.setPageSize(10);
        when(sysSystemMapper.selectById(1L)).thenReturn(new SysSystem());
        when(sysSystemService.listUserIdsBySystemId(1L)).thenReturn(Collections.singletonList(20L));
        applyOperatorVisibility();
        when(sysUserMapper.selectPage(any(Page.class), any())).thenAnswer(inv -> {
            Page<SysUser> page = inv.getArgument(0);
            page.setRecords(Collections.singletonList(operator()));
            page.setTotal(1);
            return page;
        });
        ControllerTestSupport.assertSuccess(controller.getUserBySystem(req));
    }

    @Test
    public void GET_user_unauthorizedUsersBySystem() {
        UserVo req = new UserVo();
        req.setSystemId(1L);
        req.setPageNum(1);
        req.setPageSize(10);
        when(sysSystemMapper.selectById(1L)).thenReturn(new SysSystem());
        when(sysSystemService.listUserIdsBySystemId(1L)).thenReturn(Collections.singletonList(20L));
        applyOperatorVisibility();
        when(sysUserMapper.selectPage(any(Page.class), any())).thenAnswer(inv -> {
            Page<SysUser> page = inv.getArgument(0);
            page.setRecords(Collections.emptyList());
            page.setTotal(0);
            return page;
        });
        ControllerTestSupport.assertSuccess(controller.unauthorizedUsersBySystem(req));
    }

    @Test
    public void PUT_user_addUserRole() {
        UserRoleVo vo = new UserRoleVo();
        vo.setRoleId(1L);
        vo.setUserIds(Collections.singletonList(20L));
        ControllerTestSupport.assertSuccess(controller.addUserRole(vo));
    }

    @Test
    public void GET_user_getUserByRole() {
        UserVo req = new UserVo();
        req.setRoleId(1L);
        req.setPageNum(1);
        req.setPageSize(10);
        when(sysUserRoleMapper.selectList(any())).thenReturn(Collections.emptyList());
        ControllerTestSupport.assertSuccess(controller.getUserByRole(req));
    }

    @Test
    public void PUT_user_removeUsers() {
        UserRoleVo vo = new UserRoleVo();
        vo.setRoleId(1L);
        vo.setUserIds(Collections.singletonList(20L));
        ControllerTestSupport.assertSuccess(controller.removeUsers(vo));
    }

    @Test
    public void GET_user_unauthorizedUsers() {
        UserVo req = new UserVo();
        req.setRoleId(1L);
        req.setPageNum(1);
        req.setPageSize(10);
        when(sysUserRoleMapper.selectList(any())).thenReturn(Collections.emptyList());
        applyOperatorVisibility();
        when(sysUserMapper.selectPage(any(Page.class), any())).thenAnswer(inv -> {
            Page<SysUser> page = inv.getArgument(0);
            page.setRecords(Collections.emptyList());
            page.setTotal(0);
            return page;
        });
        ControllerTestSupport.assertSuccess(controller.unauthorizedUsers(req));
    }

    @Test
    public void PUT_user_changePassword_self() {
        try (MockedStatic<SecurityUtils> shiro = ShiroMockSupport.mockUser("operator", 20L)) {
            SysUser user = operator();
            user.setPassword(SHA256Util.sha256("123456", SHA256Util.SALT));
            user.setSalt(SHA256Util.SALT);
            when(sysUserMapper.selectById(20L)).thenReturn(user);
            SysUser req = new SysUser();
            req.setOldPassword("123456");
            req.setPassword("newpass");
            ControllerTestSupport.stubUpdate(sysUserMapper);
            ControllerTestSupport.assertSuccess(controller.changePassword(req));
        }
    }

    @Test
    public void PUT_user_resetPassword_admin() {
        SysUser target = operator();
        target.setId(30L);
        when(sysUserMapper.selectById(30L)).thenReturn(target);
        when(userService.canViewUser(target)).thenReturn(true);
        SysUser req = new SysUser();
        req.setId(30L);
        req.setPassword("admin123");
        ControllerTestSupport.stubUpdate(sysUserMapper);
        ControllerTestSupport.assertSuccess(controller.resetPassword(req));
    }

    @Test
    public void GET_user_getSystemByUserId_deniesSuperadminForNormalViewer() {
        SysUser superadmin = new SysUser();
        superadmin.setId(1L);
        superadmin.setUserName(CommonConstant.SUPER_ADMIN);
        when(sysUserMapper.selectById(1L)).thenReturn(superadmin);
        when(userService.canViewUser(superadmin)).thenReturn(false);
        Assert.assertEquals((int) ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), controller.getSystemByUserId(1L).getCode());
    }

    @Test
    public void PUT_user_insertUserSystem_skipsSuperadmin() {
        SysUser superadmin = new SysUser();
        superadmin.setId(1L);
        superadmin.setUserName(CommonConstant.SUPER_ADMIN);
        when(sysUserMapper.selectById(1L)).thenReturn(superadmin);
        when(userService.canViewUser(superadmin)).thenReturn(true);
        UserSystemVo vo = new UserSystemVo();
        vo.setUserId(1L);
        MoliResult<Boolean> result = controller.insertUserSystem(vo);
        ControllerTestSupport.assertSuccess(result);
        verify(sysSystemService, never()).assignUserSystems(any(), any());
    }
}
