package com.moli.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moli.common.constant.CommonConstant;
import com.moli.common.domain.entity.SysAction;
import com.moli.common.domain.entity.SysMenu;
import com.moli.common.domain.entity.SysRoleAction;
import com.moli.common.domain.entity.SysRoleMenu;
import com.moli.common.domain.vo.RoleAuthVo;
import com.moli.common.exception.BaseException;
import com.moli.system.mapper.ActionMapper;
import com.moli.system.mapper.MenuMapper;
import com.moli.system.mapper.RoleActionMapper;
import com.moli.system.mapper.RoleMenuMapper;
import com.moli.system.service.RoleAuthService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleAuthServiceImpl implements RoleAuthService {

    private static final String MENU_TYPE_BUTTON = "F";

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private RoleActionMapper roleActionMapper;

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private ActionMapper actionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleAuth(Long roleId, List<Long> menuIds, List<String> actionCodes) {
        if (roleId == null) {
            throw new BaseException("角色ID不能为空");
        }
        List<Long> mcMenuIds = resolveMcMenuIds(menuIds);
        List<String> codes = normalizeActionCodes(actionCodes);
        validateActionCodes(codes, mcMenuIds);

        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        for (Long menuId : mcMenuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }

        roleActionMapper.delete(new LambdaQueryWrapper<SysRoleAction>().eq(SysRoleAction::getRoleId, roleId));
        for (String permCode : codes) {
            SysRoleAction roleAction = new SysRoleAction();
            roleAction.setRoleId(roleId);
            roleAction.setPermCode(permCode);
            roleActionMapper.insert(roleAction);
        }
    }

    @Override
    public RoleAuthVo getRoleAuth(Long roleId) {
        RoleAuthVo vo = new RoleAuthVo();
        if (roleId == null) {
            vo.setMenuIds(Collections.emptyList());
            vo.setActionCodes(Collections.emptyList());
            return vo;
        }
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        List<Long> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        vo.setMenuIds(resolveMcMenuIds(menuIds));

        List<SysRoleAction> roleActions = roleActionMapper.selectList(
                new LambdaQueryWrapper<SysRoleAction>().eq(SysRoleAction::getRoleId, roleId));
        if (CollectionUtils.isEmpty(roleActions)) {
            vo.setActionCodes(Collections.emptyList());
            return vo;
        }
        Set<String> enabledCodes = loadEnabledActionCodes(
                roleActions.stream().map(SysRoleAction::getPermCode).collect(Collectors.toList()));
        vo.setActionCodes(new ArrayList<>(enabledCodes));
        return vo;
    }

    private List<Long> resolveMcMenuIds(List<Long> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return Collections.emptyList();
        }
        List<SysMenu> menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getId, menuIds));
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        return menus.stream()
                .filter(menu -> !MENU_TYPE_BUTTON.equals(menu.getMenuType()))
                .map(SysMenu::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> normalizeActionCodes(List<String> actionCodes) {
        if (CollectionUtils.isEmpty(actionCodes)) {
            return Collections.emptyList();
        }
        return actionCodes.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateActionCodes(List<String> actionCodes, List<Long> menuIds) {
        if (CollectionUtils.isEmpty(actionCodes)) {
            return;
        }
        Set<String> enabledCodes = loadEnabledActionCodes(actionCodes);
        for (String code : actionCodes) {
            if (!enabledCodes.contains(code)) {
                throw new BaseException("非法或未启用的动作权限: " + code);
            }
        }
        List<SysAction> actions = actionMapper.selectList(new LambdaQueryWrapper<SysAction>()
                .in(SysAction::getPermCode, actionCodes)
                .eq(SysAction::getStatus, CommonConstant.YES));
        Set<Long> menuIdSet = new HashSet<>(menuIds);
        for (SysAction action : actions) {
            if (action.getMenuId() != null && !menuIdSet.contains(action.getMenuId())) {
                throw new BaseException("动作「" + action.getName() + "」须先勾选对应页面");
            }
        }
    }

    private Set<String> loadEnabledActionCodes(List<String> actionCodes) {
        if (CollectionUtils.isEmpty(actionCodes)) {
            return Collections.emptySet();
        }
        List<SysAction> actions = actionMapper.selectList(new LambdaQueryWrapper<SysAction>()
                .in(SysAction::getPermCode, actionCodes)
                .eq(SysAction::getStatus, CommonConstant.YES));
        return actions.stream().map(SysAction::getPermCode).collect(Collectors.toSet());
    }
}
