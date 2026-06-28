package com.moli.system.controller;

import com.moli.common.constant.CommonConstant;
import com.moli.common.constant.PermissionConstants;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysMenu;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.vo.MenuVo;
import com.moli.config.util.ShiroUtils;
import com.moli.system.mapper.MenuMapper;
import com.moli.system.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;


@RestController
@RequestMapping("/menu")
@Api(tags = "菜单管理")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuMapper menuMapper;


    @GetMapping("getRouters")
    @ApiOperation(value = "获取菜单列表", notes = "获取菜单列表")
    public MoliResult<List<MenuVo>> getRouters() {
        Long userId = ShiroUtils.getUserInfo().getId();
        SysUser sysUser = ShiroUtils.getUserInfo();
        if (CommonConstant.hasFullPermission(sysUser.getUserName())) {
            return MoliResult.success(menuService.getMenuTreeAll());
        }
        List<MenuVo> menuVoList = menuService.selectMenuTreeByUserId(userId);

        return MoliResult.success(menuVoList);
    }


    @GetMapping("list")
    @RequiresPermissions(PermissionConstants.SYSTEM_MENU_LIST)
    @ApiOperation(value = "获取菜单列表", notes = "获取菜单列表")
    public MoliResult list(String menuName, Integer status) {
        MenuVo menuVo = new MenuVo();
        menuVo.setMenuName(menuName);
        menuVo.setStatus(status);
        List<MenuVo> menuVoList = menuService.selectMenuList(menuVo);
        return MoliResult.success(menuVoList);
    }

    @PostMapping
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_MENU_ADD, PermissionConstants.SYSTEM_MENU_LIST}, logical = Logical.AND)
    @ApiOperation(value = "添加菜单", notes = "添加菜单")
    public MoliResult<Boolean> insert(@RequestBody SysMenu menu) {
        menuService.insert(menu);
        return MoliResult.success(Boolean.TRUE);
    }

    @PutMapping
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_MENU_EDIT, PermissionConstants.SYSTEM_MENU_LIST}, logical = Logical.AND)
    @ApiOperation(value = "更新菜单", notes = "更新菜单")
    public MoliResult<Boolean> update(@RequestBody SysMenu menu) {
        menuService.update(menu);
        return MoliResult.success(Boolean.TRUE);
    }

    @GetMapping(value = "/{id}")
    @RequiresPermissions(PermissionConstants.SYSTEM_MENU_LIST)
    @ApiOperation(value = "查询菜单", notes = "查询菜单")
    public MoliResult<SysMenu> getInfo(@PathVariable Long id) {

        return MoliResult.success(menuMapper.selectById(id));
    }

    @DeleteMapping("/{id}")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_MENU_REMOVE, PermissionConstants.SYSTEM_MENU_LIST}, logical = Logical.AND)
    @ApiOperation(value = "删除菜单", notes = "删除菜单")
    public MoliResult remove(@PathVariable Long id) {
        menuMapper.deleteById(id);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 根据角色获取该角色下的所有菜单
     * @return 全部菜单列表及该角色下的所有菜单id
     */
    @GetMapping("selectMenuTreeByRoleId/{roleId}")
    @RequiresPermissions(value = {
            PermissionConstants.SYSTEM_MENU_LIST,
            PermissionConstants.SYSTEM_ROLE_LIST
    }, logical = Logical.OR)
    @ApiOperation(value = "根据角色获取该角色下的所有菜单", notes = "根据角色获取该角色下的所有菜单")
    public MoliResult selectMenuTreeByRoleId(@PathVariable Long roleId) {
        List<MenuVo> menuVoList = menuService.selectMenuTreeByRoleId(roleId);
        return MoliResult.success(menuVoList);
    }
    /**
     * 获取菜单列表
     *
     * @return 菜单列表
     */
    @GetMapping("getMenuTreeAll")
    @RequiresPermissions(value = {
            PermissionConstants.SYSTEM_MENU_LIST,
            PermissionConstants.SYSTEM_ROLE_LIST
    }, logical = Logical.OR)
    @ApiOperation(value = "获取菜单列表", notes = "角色授权与菜单管理均可调用")
    public MoliResult getMenuTreeAll() {

        List<MenuVo> menuVoList = menuService.getMenuTreeAll();
        return MoliResult.success(menuVoList);
    }
}

