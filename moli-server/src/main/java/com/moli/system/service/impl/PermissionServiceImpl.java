package com.moli.system.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.moli.common.constant.CommonConstant;

import com.moli.common.domain.entity.SysAction;

import com.moli.common.domain.entity.SysMenu;

import com.moli.common.domain.entity.SysRole;

import com.moli.common.domain.entity.SysRoleAction;

import com.moli.common.domain.entity.SysRoleMenu;

import com.moli.common.domain.entity.SysUserRole;

import com.moli.common.domain.vo.CapabilitiesVo;

import com.moli.system.mapper.ActionMapper;

import com.moli.system.mapper.MenuMapper;

import com.moli.system.mapper.RoleActionMapper;

import com.moli.system.mapper.RoleMapper;

import com.moli.system.mapper.RoleMenuMapper;

import com.moli.system.mapper.SysUserRoleMapper;

import com.moli.system.service.PermissionService;

import org.apache.commons.collections.CollectionUtils;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;



import javax.annotation.Resource;

import java.util.ArrayList;

import java.util.Collections;

import java.util.HashSet;

import java.util.List;

import java.util.Set;

import java.util.stream.Collectors;



@Service

public class PermissionServiceImpl implements PermissionService {



    private static final String SUPER_ADMIN_PERMISSION = "*:*:*";

    private static final String MENU_TYPE_PAGE = "C";



    @Resource

    private SysUserRoleMapper sysUserRoleMapper;



    @Resource

    private RoleMenuMapper roleMenuMapper;



    @Resource

    private RoleActionMapper roleActionMapper;



    @Resource

    private ActionMapper actionMapper;



    @Resource

    private RoleMapper roleMapper;



    @Resource

    private MenuMapper menuMapper;



    @Override

    public Set<String> getPermissionsByUserId(Long userId, String userName) {

        if (CommonConstant.hasFullPermission(userName)) {

            Set<String> permissions = new HashSet<>();

            permissions.add(SUPER_ADMIN_PERMISSION);

            return permissions;

        }

        if (userId == null) {

            return Collections.emptySet();

        }

        List<Long> roleIdList = loadEnabledRoleIds(userId);

        if (CollectionUtils.isEmpty(roleIdList)) {

            return Collections.emptySet();

        }

        Set<String> permissions = new HashSet<>();

        permissions.addAll(loadPagePerms(roleIdList));

        permissions.addAll(loadActionPerms(roleIdList));

        return permissions;

    }



    @Override

    public CapabilitiesVo buildCapabilities(Long userId, String userName) {

        CapabilitiesVo vo = new CapabilitiesVo();

        boolean fullPermission = CommonConstant.hasFullPermission(userName);

        vo.setFullPermission(fullPermission);

        Set<String> perms = getPermissionsByUserId(userId, userName);

        vo.setPermissions(new ArrayList<>(perms));

        return vo;

    }



    private List<Long> loadEnabledRoleIds(Long userId) {

        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(

                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));

        if (CollectionUtils.isEmpty(userRoleList)) {

            return Collections.emptyList();

        }

        List<Long> roleIdList = userRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        List<SysRole> enabledRoles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>()

                .in(SysRole::getId, roleIdList)

                .eq(SysRole::getStatus, CommonConstant.YES));

        if (CollectionUtils.isEmpty(enabledRoles)) {

            return Collections.emptyList();

        }

        return enabledRoles.stream().map(SysRole::getId).collect(Collectors.toList());

    }



    private Set<String> loadPagePerms(List<Long> roleIdList) {

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

            if (MENU_TYPE_PAGE.equals(menu.getMenuType()) && StringUtils.isNotBlank(menu.getPerms())) {

                permissions.add(menu.getPerms().trim());

            }

        }

        return permissions;

    }



    private Set<String> loadActionPerms(List<Long> roleIdList) {

        List<SysRoleAction> roleActions = roleActionMapper.selectList(

                new LambdaQueryWrapper<SysRoleAction>().in(SysRoleAction::getRoleId, roleIdList));

        if (CollectionUtils.isEmpty(roleActions)) {

            return Collections.emptySet();

        }

        List<String> codes = roleActions.stream()

                .map(SysRoleAction::getPermCode)

                .filter(StringUtils::isNotBlank)

                .distinct()

                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(codes)) {

            return Collections.emptySet();

        }

        List<SysAction> enabledActions = actionMapper.selectList(new LambdaQueryWrapper<SysAction>()

                .in(SysAction::getPermCode, codes)

                .eq(SysAction::getStatus, CommonConstant.YES));

        return enabledActions.stream().map(SysAction::getPermCode).collect(Collectors.toSet());

    }

}

