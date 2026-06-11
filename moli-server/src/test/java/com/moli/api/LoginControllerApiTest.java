package com.moli.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.enums.ResponseCodeEnums;
import com.moli.system.controller.LoginController;
import com.moli.system.mapper.SysLoginLogMapper;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.service.MenuService;
import com.moli.system.service.SysSystemService;
import com.moli.config.util.RedisUtil;
import com.moli.testsupport.AbstractApiTest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerApiTest extends AbstractApiTest {

    @InjectMocks
    private LoginController controller;

    @Mock
    private MenuService menuService;
    @Mock
    private SysSystemService sysSystemService;
    @Mock
    private SysUserMapper sysUserMapper;
    @Mock
    private RedisUtil redisUtil;
    @Mock
    private SysLoginLogMapper sysLoginLogMapper;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(controller, "captchaEnabled", false);
    }

    @Test
    public void POST_login_userNotFound() {
        LoginController spyController = spy(controller);
        doNothing().when(spyController).insertLoginLog(any(), anyString(), anyInt());
        when(sysUserMapper.selectOne(any())).thenReturn(null);
        SysUser req = new SysUser();
        req.setUserName("nobody");
        Assert.assertEquals((int) ResponseCodeEnums.ERROR.getCode(), spyController.login(req).getCode());
    }

    @Test
    public void POST_captchaImage_disabled() {
        Assert.assertEquals((int) ResponseCodeEnums.SERVICE_ERROR_CODE.getCode(), controller.captchaImage().getCode());
    }

    @Test
    public void POST_logout() {
        Subject subject = mock(Subject.class);
        doNothing().when(subject).logout();
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getSubject).thenReturn(subject);
            Assert.assertEquals(200, controller.logout().getCode());
        }
    }
}
