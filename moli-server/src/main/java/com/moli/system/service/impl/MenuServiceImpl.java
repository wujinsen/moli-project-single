package com.moli.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moli.common.constant.CommonConstant;
import com.moli.common.domain.entity.SysMenu;
import com.moli.common.domain.entity.SysRole;
import com.moli.common.domain.entity.SysRoleMenu;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.entity.SysUserRole;
import com.moli.common.domain.vo.MenuMetaVo;
import com.moli.common.domain.vo.MenuVo;
import com.moli.system.mapper.MenuMapper;
import com.moli.system.mapper.RoleMapper;
import com.moli.system.mapper.RoleMenuMapper;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.mapper.SysUserRoleMapper;
import com.moli.system.service.MenuService;
import com.moli.common.utils.I18nUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;


    @Override
    public Boolean insert(SysMenu menu) {
        menuMapper.insert(menu);
        return Boolean.TRUE;
    }

    @Override
    public Boolean update(SysMenu menu) {
        menuMapper.updateById(menu);
        return Boolean.TRUE;
    }

    @Override
    public List<MenuVo> selectMenuTreeByUserId(Long userId) {

        List<MenuVo> menuVoList = new ArrayList<>();
        MenuVo menuVo = new MenuVo();
        menuVo.setUserId(userId);
        menuVoList = this.selectMenuListByUserId(menuVo);
        return createTree(menuVoList);

    }

    @Override
    public List<MenuVo> selectMenuList(MenuVo menuVo) {
        LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(menuVo.getMenuName())) {
            lambdaQueryWrapper.like(SysMenu::getMenuName, menuVo.getMenuName());
        }
        if (menuVo.getStatus() != null) {
            lambdaQueryWrapper.eq(SysMenu::getStatus, menuVo.getStatus());
        }
        lambdaQueryWrapper.orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> menuList = menuMapper.selectList(lambdaQueryWrapper);
        List<MenuVo> menuVoList = new ArrayList<>();
        menuList.forEach(e -> menuVoList.add(toMenuVo(e, false)));
        return menuVoList;
    }

    @Override
    public List<MenuVo> selectMenuListByUserId(MenuVo menuVo) {
        SysUser user = sysUserMapper.selectById(menuVo.getUserId());
        //超级管理员
        if (CommonConstant.isSuperAdmin(user.getUserName())) {
            List<SysMenu> menuList = menuMapper.selectList(new LambdaQueryWrapper<>());
            List<MenuVo> menuVoList = new ArrayList<>();
            menuList.forEach(e -> menuVoList.add(toMenuVo(e, true)));
            return menuVoList;
        }
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, menuVo.getUserId()));
        if (CollectionUtils.isEmpty(userRoleList)) {
            return new ArrayList<>();
        }
        List<Long> roleIdList = userRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<SysRole> enabledRoles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getId, roleIdList)
                .eq(SysRole::getStatus, CommonConstant.YES));
        if (CollectionUtils.isEmpty(enabledRoles)) {
            return new ArrayList<>();
        }
        roleIdList = enabledRoles.stream().map(SysRole::getId).collect(Collectors.toList());
        List<SysRoleMenu> roleMenuList = roleMenuMapper.selectList(new QueryWrapper<SysRoleMenu>().lambda().in(SysRoleMenu::getRoleId, roleIdList));
        List<Long> menuIdList = roleMenuList.stream().map(SysRoleMenu::getMenuId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(menuIdList)) {
            return new ArrayList<>();
        }
        List<SysMenu> menuList = loadMenusWithAncestors(menuIdList);
        List<MenuVo> menuVoList = new ArrayList<>();
        menuList.forEach(e -> menuVoList.add(toMenuVo(e, true)));
        return menuVoList;
    }

    /**
     * 角色仅勾选子菜单时，自动补齐父级目录，否则前端树形菜单无法展示。
     */
    private List<SysMenu> loadMenusWithAncestors(List<Long> menuIdList) {
        Set<Long> allIds = new LinkedHashSet<>(menuIdList);
        List<Long> pending = new ArrayList<>(menuIdList);

        while (!pending.isEmpty()) {
            Long menuId = pending.remove(pending.size() - 1);
            SysMenu menu = menuMapper.selectById(menuId);
            if (menu == null || menu.getParentId() == null || menu.getParentId() <= 0) {
                continue;
            }
            if (allIds.add(menu.getParentId())) {
                pending.add(menu.getParentId());
            }
        }

        return menuMapper.selectList(new QueryWrapper<SysMenu>().lambda()
                .in(SysMenu::getId, allIds)
                .eq(SysMenu::getStatus, CommonConstant.YES)
                .orderByAsc(SysMenu::getOrderNum));
    }

    @Override
    public List<MenuVo> selectMenuTreeByRoleId(Long roleId) {

        List<SysRoleMenu> roleMenuList = roleMenuMapper.selectList(new QueryWrapper<SysRoleMenu>().lambda().eq(SysRoleMenu::getRoleId, roleId));
        List<Long> menuIdList = roleMenuList.stream().map(e -> e.getMenuId()).collect(Collectors.toList());
        List<MenuVo> menuVoList = this.getMenuTreeAll();
        createTree(menuVoList);
        if (CollectionUtils.isNotEmpty(menuIdList)) {
            menuVoList.get(0).setMenuIds(menuIdList);
        }
        return menuVoList;
    }

    @Override
    public List<MenuVo> getMenuTreeAll() {
        List<SysMenu> menuList = menuMapper.selectList(new QueryWrapper<>());
        List<MenuVo> menuVoList = new ArrayList<>();
        menuList.forEach(e -> menuVoList.add(toMenuVo(e, true)));
        return createTree(menuVoList);
    }

    private MenuVo toMenuVo(SysMenu menu, boolean localize) {
        String lang = I18nUtils.resolveLanguage();
        MenuVo menuVo = new MenuVo();
        BeanUtils.copyProperties(menu, menuVo);
        if (localize) {
            menuVo.setMenuName(I18nUtils.resolveLocalizedText(
                    menu.getMenuName(), menu.getMenuNameEn(), menu.getMenuNameJa(), lang));
        }
        menuVo.setHidden(!CommonConstant.YES.equals(menu.getStatus()));
        menuVo.setName(getRouteName(menuVo));
        menuVo.setPath(getRouterPath(menuVo));
        menuVo.setComponent(getComponent(menuVo));
        menuVo.setRedirect(CommonConstant.NO_REDIRECT);
        return menuVo;
    }

    /**
     * 递归查询一级节点
     */
    private static List<MenuVo> createTree(List<MenuVo> deptList) {
        List<MenuVo> list = new ArrayList<>();
        for (MenuVo treeNode : deptList) {
            MenuMetaVo menuMetaVo = new MenuMetaVo();
            if (treeNode.getParentId().longValue() == 0) {
                menuMetaVo.setTitle(treeNode.getMenuName());
                menuMetaVo.setIcon(treeNode.getIcon());
                menuMetaVo.setNoCache(false);
                treeNode.setMeta(menuMetaVo);
                treeNode.setAlwaysShow(true);
                list.add(findChildrenTree(treeNode, deptList));
            } else {
                menuMetaVo.setTitle(treeNode.getMenuName());
                menuMetaVo.setIcon(treeNode.getIcon());
                menuMetaVo.setNoCache(false);
                treeNode.setMeta(menuMetaVo);
            }
        }
        return list;
    }

    /**
     * 递归查找当前节点下的所有子节点
     *
     * @param htgMenuVo 当前节点
     * @param deptList  所有节点
     * @return
     */
    private static MenuVo findChildrenTree(MenuVo htgMenuVo, List<MenuVo> deptList) {
        List<MenuVo> childrenList = new ArrayList<>();
        for (MenuVo childrenNode : deptList) {
            if (htgMenuVo.getId().longValue() == childrenNode.getParentId().longValue()) {
                childrenList.add(childrenNode);
            }
        }
        if (CollectionUtils.isNotEmpty(childrenList)) {
            htgMenuVo.setChildren(childrenList);
            for (MenuVo menuVoTwo : childrenList) {
                findChildrenTree(menuVoTwo, deptList);
            }
        }
        return htgMenuVo;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    private String getComponent(MenuVo menu) {
        String component = CommonConstant.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent())) {
            component = menu.getComponent();
        }
//        else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0) {
//            component = CommonConstant.INNER_LINK;
//        }
        //组件内容为空, 不是一级菜单并且菜单类型为目录
        else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = CommonConstant.PARENT_VIEW;
        }
        return component;
    }


    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    private String getRouteName(MenuVo menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    private String getRouterPath(MenuVo menu) {

        String routerPath = menu.getPath();
        // 一级菜单并且菜单类型为目录
        if (0 == menu.getParentId().intValue() && CommonConstant.TYPE_DIR.equals(menu.getMenuType())) {
            routerPath = "/" + menu.getPath();
        }
        return routerPath;
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    private boolean isParentView(MenuVo menu) {
        //不是一级菜单并且菜单类型为目录
        return menu.getParentId().intValue() != 0 && CommonConstant.TYPE_DIR.equals(menu.getMenuType());
    }

}
