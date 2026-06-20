package com.moli.api;

import com.moli.common.domain.entity.SysRole;
import com.moli.common.domain.vo.RoleVo;
import com.moli.common.domain.vo.SysRoleVo;
import com.moli.system.controller.RoleController;
import com.moli.system.mapper.RoleActionMapper;
import com.moli.system.mapper.RoleMapper;
import com.moli.system.mapper.RoleMenuMapper;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.mapper.SysUserRoleMapper;
import com.moli.system.service.RoleAuthService;
import com.moli.testsupport.AbstractApiTest;
import com.moli.testsupport.ControllerTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoleControllerApiTest extends AbstractApiTest {

    @InjectMocks
    private RoleController controller;

    @Mock
    private RoleMapper roleMapper;
    @Mock
    private RoleMenuMapper roleMenuMapper;
    @Mock
    private RoleActionMapper roleActionMapper;
    @Mock
    private RoleAuthService roleAuthService;
    @Mock
    private SysUserRoleMapper sysUserRoleMapper;
    @Mock
    private SysUserMapper sysUserMapper;

    @Test
    public void GET_role_list() {
        ControllerTestSupport.stubEmptyPage(roleMapper);
        RoleVo vo = new RoleVo();
        vo.setPageNum(1);
        vo.setPageSize(10);
        ControllerTestSupport.assertSuccess(controller.list(vo));
    }

    @Test
    public void POST_role_insert() {
        when(roleMapper.insert(any(SysRole.class))).thenAnswer(inv -> {
            ((SysRole) inv.getArgument(0)).setId(1L);
            return 1;
        });
        ControllerTestSupport.assertSuccess(controller.insert(new RoleVo()));
    }

    @Test
    public void PUT_role_update() {
        try (org.mockito.MockedStatic<org.apache.shiro.SecurityUtils> shiro =
                     org.mockito.Mockito.mockStatic(org.apache.shiro.SecurityUtils.class)) {
            org.apache.shiro.subject.Subject subject = org.mockito.Mockito.mock(org.apache.shiro.subject.Subject.class);
            org.mockito.Mockito.when(subject.isPermitted(org.mockito.ArgumentMatchers.anyString())).thenReturn(true);
            shiro.when(org.apache.shiro.SecurityUtils::getSubject).thenReturn(subject);
            ControllerTestSupport.stubUpdate(roleMapper);
            ControllerTestSupport.stubSelectListEmpty(sysUserRoleMapper);
            SysRoleVo vo = new SysRoleVo();
            vo.setId(1L);
            ControllerTestSupport.assertSuccess(controller.update(vo));
        }
    }

    @Test
    public void GET_role_id() {
        ControllerTestSupport.stubSelectById(roleMapper, new SysRole());
        ControllerTestSupport.assertSuccess(controller.getInfo(1L));
    }

    @Test
    public void DELETE_role_ids() {
        ControllerTestSupport.assertSuccess(controller.delete(new Long[]{1L}));
    }

    @Test
    public void PUT_role_changeStatus() {
        ControllerTestSupport.stubUpdate(roleMapper);
        ControllerTestSupport.assertSuccess(controller.changeStatus(new SysRole()));
    }

    @Test
    public void GET_role_getRoleAll() {
        ControllerTestSupport.stubSelectListEmpty(roleMapper);
        ControllerTestSupport.assertSuccess(controller.getRoleAll());
    }
}
