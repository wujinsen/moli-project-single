package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.Role;
import com.moli.common.domain.vo.RoleVo;
import com.moli.common.page.PageRes;
import com.moli.system.mapper.RoleMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
@Api(tags = "角色管理")
public class RoleController {

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 角色列表
     *
     * @param roleVo
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "角色列表", notes = "角色列表")
    public MoliResult<PageRes<Role>> list(RoleVo roleVo) {
        PageRes<Role> result = new PageRes<>();
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (roleVo.getRoleName() != null) {
            lambdaQueryWrapper.eq(Role::getRoleName, roleVo.getRoleName());
        }
        if (roleVo.getStatus() != null) {
            lambdaQueryWrapper.eq(Role::getStatus, roleVo.getStatus());
        }
        if (roleVo.getBeginTime() != null) {
            lambdaQueryWrapper.between(Role::getCreateTime, roleVo.getBeginTime() + " 00:00:00", roleVo.getEndTime() + " 23:59:59");
        }
        Page page = new Page();
        roleMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setItems(page.getRecords());
        return MoliResult.success(result);
    }

    /**
     * 添加角色
     *
     * @return 添加角色
     */
    @PostMapping
    public MoliResult<Boolean> insert(@RequestBody Role role) {
        roleMapper.insert(role);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 更新角色
     *
     * @return
     */
    @PutMapping
    public MoliResult<Boolean> update(@RequestBody Role role) {
        roleMapper.updateById(role);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 查询单个角色
     */
    @GetMapping(value = "/{id}")
    public MoliResult<Role> getInfo(@PathVariable Long id) {
        return MoliResult.success(roleMapper.selectById(id));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public MoliResult remove(@PathVariable("id") Long id) {
        roleMapper.deleteById(id);
        return MoliResult.success(Boolean.TRUE);
    }


}
