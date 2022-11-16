package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysRole;
import com.moli.common.domain.entity.SysRoleMenu;
import com.moli.common.domain.vo.RoleVo;
import com.moli.common.enums.BusinessTypeEnum;
import com.moli.common.log.MoliLog;
import com.moli.common.page.PageRes;
import com.moli.system.mapper.RoleMapper;
import com.moli.system.mapper.RoleMenuMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
@Api(tags = "角色管理")
public class RoleController {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    /**
     * 角色列表
     *
     * @param roleVo
     * @return
     */
    @GetMapping("/list")
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
    @ApiOperation(value = "添加角色", notes = "添加角色")
    public MoliResult<Boolean> insert(@RequestBody RoleVo roleVo) {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleVo, role);
        roleMapper.insert(role);
        if (CollectionUtils.isNotEmpty(roleVo.getMenuIds())) {
            for (Long menuId : roleVo.getMenuIds()) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getId());
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
        return MoliResult.success(Boolean.TRUE);
    }


    @PutMapping
    @MoliLog(title = "角色管理", businessType = BusinessTypeEnum.UPDATE)
    @ApiOperation(value = "更新角色", notes = "更新角色")
    public MoliResult<Boolean> update(@RequestBody SysRole role) {
        roleMapper.updateById(role);
        return MoliResult.success(Boolean.TRUE);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询单个角色", notes = "查询单个角色")
    public MoliResult<SysRole> getInfo(@PathVariable Long id) {
        return MoliResult.success(roleMapper.selectById(id));
    }

    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除角色", notes = "删除角色")
    public MoliResult delete(@PathVariable Long[] ids) {
        for (Long roleId : ids) {
            roleMapper.deleteById(roleId);
            roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        }
        return MoliResult.success(Boolean.TRUE);
    }

    @PutMapping("/changeStatus")
    @ApiOperation(value = "角色状态变更", notes = "角色状态变更")
    public MoliResult changeStatus(@RequestBody SysRole role) {
        roleMapper.updateById(role);
        return MoliResult.success(Boolean.TRUE);
    }

}
