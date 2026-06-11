package com.moli.api;

import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysSystem;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.vo.SystemEnterReq;
import com.moli.common.domain.vo.SystemEnterVo;
import com.moli.common.domain.vo.SystemVo;
import com.moli.common.page.PageRes;
import com.moli.system.controller.SystemController;
import com.moli.system.service.SysSystemService;
import com.moli.testsupport.ControllerTestSupport;
import com.moli.testsupport.AbstractApiTest;
import com.moli.testsupport.ShiroMockSupport;
import org.apache.shiro.SecurityUtils;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SystemControllerApiTest extends AbstractApiTest {

    @InjectMocks
    private SystemController controller;

    @Mock
    private SysSystemService sysSystemService;

    @Test
    public void GET_system_my() {
        try (MockedStatic<SecurityUtils> shiro = ShiroMockSupport.mockUser("operator", 2L)) {
            when(sysSystemService.listByUserId(2L, "operator")).thenReturn(Collections.<SystemVo>emptyList());
            ControllerTestSupport.assertSuccess(controller.mySystems());
        }
    }

    @Test
    public void POST_system_enter() {
        try (MockedStatic<SecurityUtils> shiro = ShiroMockSupport.mockUser("operator", 2L)) {
            SystemEnterReq req = new SystemEnterReq();
            req.setSystemId(1L);
            when(sysSystemService.enterSystem(2L, "operator", 1L)).thenReturn(new SystemEnterVo());
            ControllerTestSupport.assertSuccess(controller.enter(req));
        }
    }

    @Test
    public void POST_system_switch() {
        try (MockedStatic<SecurityUtils> shiro = ShiroMockSupport.mockUser("operator", 2L)) {
            SystemEnterReq req = new SystemEnterReq();
            req.setSystemId(1L);
            when(sysSystemService.enterSystem(2L, "operator", 1L)).thenReturn(new SystemEnterVo());
            ControllerTestSupport.assertSuccess(controller.switchSystem(req));
        }
    }

    @Test
    public void GET_system_list() {
        when(sysSystemService.page(any(SysSystem.class))).thenReturn(new PageRes<>());
        ControllerTestSupport.assertSuccess(controller.list(new SysSystem()));
    }

    @Test
    public void POST_system_insert() {
        try (MockedStatic<SecurityUtils> shiro = ShiroMockSupport.mockSuperadmin()) {
            when(sysSystemService.saveSystem(any())).thenReturn(true);
            ControllerTestSupport.assertSuccess(controller.insert(new SysSystem()));
        }
    }

    @Test
    public void PUT_system_update() {
        try (MockedStatic<SecurityUtils> shiro = ShiroMockSupport.mockSuperadmin()) {
            when(sysSystemService.updateSystem(any())).thenReturn(true);
            ControllerTestSupport.assertSuccess(controller.update(new SysSystem()));
        }
    }

    @Test
    public void DELETE_system_ids() {
        try (MockedStatic<SecurityUtils> shiro = ShiroMockSupport.mockSuperadmin()) {
            when(sysSystemService.deleteSystems(any())).thenReturn(true);
            ControllerTestSupport.assertSuccess(controller.delete("1,2"));
        }
    }
}
