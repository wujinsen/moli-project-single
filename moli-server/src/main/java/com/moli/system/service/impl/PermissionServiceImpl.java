package com.moli.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moli.common.constant.CommonConstant;
import com.moli.common.domain.entity.SysMenu;
import com.moli.common.domain.entity.SysRole;
import com.moli.common.domain.entity.SysRoleMenu;
import com.moli.common.domain.entity.SysUserRole;
import com.moli.system.mapper.MenuMapper;
import com.moli.system.mapper.RoleMapper;
import com.moli.system.mapper.RoleMenuMapper;
import com.moli.system.mapper.SysUserRoleMapper;
import com.moli.system.service.PermissionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final String SUPER_ADMIN_PERMISSION = "*:*:*";

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private MenuMapper menuMapper;

    @Override
    public Set<String> getPermissionsByUserId(Long userId, String userName) {
        if (CommonConstant.isSuperAdmin(userName)) {
            Set<String> permissions = new HashSet<>();
            permissions.add(SUPER_ADMIN_PERMISSION);
            return permissions;
        }
        if (userId == null) {
            return Collections.emptySet();
        }
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (CollectionUtils.isEmpty(userRoleList)) {
            return Collections.emptySet();
        }
        List<Long> roleIdList = userRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<SysRole> enabledRoles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getId, roleIdList)
                .eq(SysRole::getStatus, CommonConstant.YES));
        if (CollectionUtils.isEmpty(enabledRoles)) {
            return Collections.emptySet();
        }
        roleIdList = enabledRoles.stream().map(SysRole::getId).collect(Collectors.toList());
        List<SysRoleMenu> roleMenuList = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIdList));
        if (CollectionUtils.isEmpty(roleMenuList)) {
            return Collections.emptySet();
        }
        List<Long> menuIdList = roleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        List<SysMenu> menuList = menuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>().in(SysMenu::getId, menuIdList).eq(SysMenu::getStatus, 1));
        Set<String> permissions = new HashSet<>();
        for (SysMenu menu : menuList) {
            if (StringUtils.isNotBlank(menu.getPerms())) {
                permissions.add(menu.getPerms().trim());
            }
        }
        return permissions;
    }
}
