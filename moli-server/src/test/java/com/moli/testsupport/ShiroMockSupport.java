package com.moli.testsupport;

import com.moli.common.constant.CommonConstant;
import com.moli.common.domain.entity.SysUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public final class ShiroMockSupport {

    private ShiroMockSupport() {
    }

    public static MockedStatic<SecurityUtils> mockUser(String userName, Long userId) {
        MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class);
        Subject subject = mock(Subject.class);
        SysUser user = new SysUser();
        user.setId(userId);
        user.setUserName(userName);
        when(subject.getPrincipal()).thenReturn(user);
        mocked.when(SecurityUtils::getSubject).thenReturn(subject);
        return mocked;
    }

    public static MockedStatic<SecurityUtils> mockSuperadmin() {
        return mockUser(CommonConstant.SUPER_ADMIN, 1L);
    }
}
