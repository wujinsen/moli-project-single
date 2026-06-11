package com.moli.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moli.common.constant.CommonConstant;
import com.moli.common.domain.entity.SysSystem;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.entity.SysUserSystem;
import com.moli.system.mapper.SysSystemMapper;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.mapper.SysUserSystemMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SysSystemServiceImplTest {

    @InjectMocks
    private SysSystemServiceImpl sysSystemService;

    @Mock
    private SysSystemMapper sysSystemMapper;

    @Mock
    private SysUserSystemMapper sysUserSystemMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @Test
    public void listUserIdsBySystemId_nullSystemId_returnsEmpty() {
        Assert.assertTrue(sysSystemService.listUserIdsBySystemId(null).isEmpty());
    }

    @Test
    public void listUserIdsBySystemId_mergesAssignedUsersAndPrivilegedAccounts() {
        Long systemId = 100L;

        SysUserSystem relation = new SysUserSystem();
        relation.setUserId(20L);
        relation.setSystemId(systemId);
        when(sysUserSystemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(relation));

        SysUser superadmin = user(1L, CommonConstant.SUPER_ADMIN);
        SysUser legacyAdmin = user(2L, CommonConstant.LEGACY_SUPER_ADMIN);
        when(sysUserMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(superadmin, legacyAdmin));

        List<Long> userIds = sysSystemService.listUserIdsBySystemId(systemId);

        Assert.assertEquals(Arrays.asList(20L, 1L, 2L), userIds);
    }

    @Test
    public void listUserIdsBySystemId_includesPrivilegedAccountsEvenWithoutRelations() {
        Long systemId = 200L;

        when(sysUserSystemMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        SysUser superadmin = user(9L, CommonConstant.SUPER_ADMIN);
        when(sysUserMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(superadmin));

        List<Long> userIds = sysSystemService.listUserIdsBySystemId(systemId);

        Assert.assertEquals(Collections.singletonList(9L), userIds);
    }

    @Test
    public void assignUserSystems_skipsPrivilegedAccount() {
        SysUser superadmin = user(1L, CommonConstant.SUPER_ADMIN);
        when(sysUserMapper.selectById(1L)).thenReturn(superadmin);

        sysSystemService.assignUserSystems(1L, Collections.singletonList(100L));

        verify(sysUserSystemMapper, never()).delete(any(LambdaQueryWrapper.class));
        verify(sysUserSystemMapper, never()).insert(any(SysUserSystem.class));
    }

    @Test
    public void assignUserSystems_replacesRelationsForNormalUser() {
        SysUser operator = user(20L, "operator");
        when(sysUserMapper.selectById(20L)).thenReturn(operator);

        sysSystemService.assignUserSystems(20L, Arrays.asList(100L, 101L));

        verify(sysUserSystemMapper).delete(any(LambdaQueryWrapper.class));

        ArgumentCaptor<SysUserSystem> captor = ArgumentCaptor.forClass(SysUserSystem.class);
        verify(sysUserSystemMapper, org.mockito.Mockito.times(2)).insert(captor.capture());

        List<SysUserSystem> inserted = captor.getAllValues();
        Assert.assertEquals(Long.valueOf(20L), inserted.get(0).getUserId());
        Assert.assertEquals(Long.valueOf(100L), inserted.get(0).getSystemId());
        Assert.assertEquals(CommonConstant.YES, inserted.get(0).getIsDefault());
        Assert.assertEquals(Long.valueOf(101L), inserted.get(1).getSystemId());
        Assert.assertEquals(CommonConstant.NO, inserted.get(1).getIsDefault());
    }

    private static SysUser user(Long id, String userName) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUserName(userName);
        user.setIsDelete(CommonConstant.UN_DELETE);
        return user;
    }
}
