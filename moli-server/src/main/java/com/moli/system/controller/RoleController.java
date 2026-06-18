package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.constant.PermissionConstants;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysRole;
import com.moli.common.domain.entity.SysRoleAction;
import com.moli.common.domain.entity.SysRoleMenu;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.entity.SysUserRole;
import com.moli.common.domain.vo.MenuVo;
import com.moli.common.domain.vo.RoleAuthVo;
import com.moli.common.domain.vo.RoleVo;
import com.moli.common.domain.vo.SysRoleVo;
import com.moli.common.enums.BusinessTypeEnum;
import com.moli.common.log.MoliLog;
import com.moli.common.page.PageRes;
import com.moli.config.util.PermissionAuthUtils;
import com.moli.system.mapper.RoleMapper;
import com.moli.system.mapper.RoleActionMapper;
import com.moli.system.mapper.RoleMenuMapper;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.mapper.SysUserRoleMapper;
import com.moli.system.service.RoleAuthService;
import com.moli.system.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.SecurityUtils;
import com.moli.common.enums.ResponseCodeEnums;
import java.util.List;

@RestController
@RequestMapping("/role")
@Api(tags = "角色管理")
public class RoleController {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private RoleActionMapper roleActionMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RoleAuthService roleAuthService;

    @Autowired
    private MenuService menuService;

    @GetMapping("/auth/menu-tree")
    @RequiresPermissions(PermissionConstants.SYSTEM_ROLE_LIST)
    @ApiOperation(value = "角色授权用完整菜单树", notes = "仅需 system:role:list，不依赖菜单管理权限")
    public MoliResult<List<MenuVo>> authMenuTree() {
        return MoliResult.success(menuService.getMenuTreeAll());
    }

    @GetMapping("/list")
    @RequiresPermissions(PermissionConstants.SYSTEM_ROLE_LIST)
    @ApiOperation(value = "角色列表", notes = "角色列表")
    public MoliResult<PageRes<SysRole>> list(RoleVo roleVo) {

        PageRes<SysRole> result = new PageRes<>();
        LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(roleVo.getRoleName())) {
            lambdaQueryWrapper.eq(SysRole::getRoleName, roleVo.getRoleName());
        }
        if (roleVo.getStatus() != null) {
            lambdaQueryWrapper.eq(SysRole::getStatus, roleVo.getStatus());
        }
        if (roleVo.getBeginTime() != null) {
            lambdaQueryWrapper.between(SysRole::getCreateTime, roleVo.getBeginTime() + " 00:00:00", roleVo.getEndTime() + " 23:59:59");
        }
        Page page = new Page();
        page.setCurrent(roleVo.getPageNum());
        page.setSize(roleVo.getPageSize());
        roleMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(page.getRecords());
        result.setPageNum(roleVo.getPageNum());
        result.setPageSize(roleVo.getPageSize());
        return MoliResult.success(result);

    }

    @PostMapping
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_ROLE_ADD, PermissionConstants.SYSTEM_ROLE_LIST}, logical = Logical.AND)
    @ApiOperation(value = "添加角色", notes = "添加角色")
    public MoliResult<Boolean> insert(@RequestBody RoleVo roleVo) {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleVo, role);
        roleMapper.insert(role);
        roleAuthService.assignRoleAuth(role.getId(), roleVo.getMenuIds(), roleVo.getActionCodes());
        return MoliResult.success(Boolean.TRUE);
    }


    @PutMapping
    @RequiresPermissions(PermissionConstants.SYSTEM_ROLE_LIST)
    @MoliLog(title = "角色菜单授权", businessType = BusinessTypeEnum.UPDATE)
    @ApiOperation(value = "更新角色", notes = "更新角色；保存 menuIds/actionCodes 需 system:role:assignPerm，否则需 system:role:edit")
    public MoliResult<Boolean> update(@RequestBody SysRoleVo roleVo) {
        if (isRoleAuthPayload(roleVo)) {
            if (!hasRoleManagePermission(PermissionConstants.SYSTEM_ROLE_ASSIGN_PERM)) {
                return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限操作");
            }
        } else if (!hasRoleManagePermission(PermissionConstants.SYSTEM_ROLE_EDIT)) {
            return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限操作");
        }
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(roleVo, sysRole);
        roleMapper.updateById(sysRole);
        roleAuthService.assignRoleAuth(roleVo.getId(), roleVo.getMenuIds(), roleVo.getActionCodes());
        clearAuthorizationCacheByRoleId(roleVo.getId());
        return MoliResult.success(Boolean.TRUE, PermissionConstants.ROLE_ASSIGN_REFRESH_MSG);
    }

    @GetMapping("/{id}/auth")
    @RequiresPermissions(PermissionConstants.SYSTEM_ROLE_LIST)
    @ApiOperation(value = "角色授权回显", notes = "menuIds + actionCodes")
    public MoliResult<RoleAuthVo> getRoleAuth(@PathVariable Long id) {
        return MoliResult.success(roleAuthService.getRoleAuth(id));
    }

    private void clearAuthorizationCacheByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId));
        if (CollectionUtils.isEmpty(userRoleList)) {
            return;
        }
        for (SysUserRole userRole : userRoleList) {
            SysUser user = sysUserMapper.selectById(userRole.getUserId());
            if (user != null) {
                PermissionAuthUtils.clearUserAuthorizationCache(user.getUserName());
            }
        }
    }

    @GetMapping(value = "/{id}")
    @RequiresPermissions(PermissionConstants.SYSTEM_ROLE_LIST)
    @ApiOperation(value = "查询单个角色", notes = "查询单个角色")
    public MoliResult<SysRole> getInfo(@PathVariable Long id) {
        return MoliResult.success(roleMapper.selectById(id));
    }

    @DeleteMapping("/{ids}")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_ROLE_REMOVE, PermissionConstants.SYSTEM_ROLE_LIST}, logical = Logical.AND)
    @ApiOperation(value = "删除角色", notes = "删除角色")
    public MoliResult delete(@PathVariable Long[] ids) {
        for (Long roleId : ids) {
            roleMapper.deleteById(roleId);
            roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
            roleActionMapper.delete(new LambdaQueryWrapper<SysRoleAction>().eq(SysRoleAction::getRoleId, roleId));
        }
        return MoliResult.success(Boolean.TRUE);
    }

    @PutMapping("/changeStatus")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_ROLE_EDIT, PermissionConstants.SYSTEM_ROLE_LIST}, logical = Logical.AND)
    @ApiOperation(value = "角色状态变更", notes = "角色状态变更")
    public MoliResult changeStatus(@RequestBody SysRole role) {
        roleMapper.updateById(role);
        return MoliResult.success(Boolean.TRUE);
    }

    @GetMapping("/getRoleAll")
    @RequiresPermissions(PermissionConstants.SYSTEM_ROLE_LIST)
    @ApiOperation(value = "获取所有角色", notes = "获取所有角色")
    public MoliResult<List<SysRole>> getRoleAll() {

        return MoliResult.success(roleMapper.selectList(new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, CommonConstant.YES)));

    }


    private boolean isRoleAuthPayload(SysRoleVo roleVo) {
        return roleVo != null && (roleVo.getMenuIds() != null || roleVo.getActionCodes() != null);
    }

    private boolean hasRoleManagePermission(String actionPerm) {
        return SecurityUtils.getSubject().isPermitted(actionPerm)
                && SecurityUtils.getSubject().isPermitted(PermissionConstants.SYSTEM_ROLE_LIST);
    }

}
