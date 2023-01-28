package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysDept;
import com.moli.common.domain.entity.SysRole;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.entity.SysUserRole;
import com.moli.common.domain.vo.UserRoleVo;
import com.moli.common.domain.vo.UserVo;
import com.moli.common.page.PageRes;
import com.moli.common.utils.MoliDateUtils;
import com.moli.config.util.SHA256Util;
import com.moli.config.util.ShiroUtils;
import com.moli.system.mapper.RoleMapper;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.mapper.SysUserRoleMapper;
import com.moli.system.service.DeptService;
import com.moli.system.service.UserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private DeptService deptService;

    /**
     * 用户列表
     *
     * @param userVo
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "用户列表", notes = "用户列表")
    public MoliResult<PageRes<SysUser>> list(UserVo userVo) {
        PageRes<SysUser> result = new PageRes<>();
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper();

        if (userVo.getDeptId() != null) {
            List<Long> detpIdList = new ArrayList<>();
            List<SysDept> deptList = deptService.list();
            detpIdList.add(userVo.getDeptId());
            deptService.findChildrenDeptIdTree(detpIdList, deptList, userVo.getDeptId());
            lambdaQueryWrapper.in(SysUser::getDeptId, detpIdList);
        }

        if (StringUtils.isNotBlank(userVo.getUserName())) {
            lambdaQueryWrapper.eq(SysUser::getUserName,userVo.getUserName());
        }
        if (StringUtils.isNotBlank(userVo.getTelephone())) {
            lambdaQueryWrapper.eq(SysUser::getTelephone, userVo.getTelephone());
        }
        if (userVo.getStatus() != null) {
            lambdaQueryWrapper.eq(SysUser::getStatus, userVo.getStatus());
        }
        if (userVo.getBeginTime() != null) {
            lambdaQueryWrapper.between(SysUser::getCreateTime, MoliDateUtils.startTimeToDateStart(userVo.getBeginTime()), userVo.getEndTime() + " 23:59:59");
        }
        lambdaQueryWrapper.eq(SysUser::getIsDelete, CommonConstant.UN_DELETE);
        Page page = new Page();
        page.setCurrent(userVo.getPageNum());
        page.setSize(userVo.getPageSize());
        sysUserMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(page.getRecords());
        result.setPageNum(userVo.getPageNum());
        result.setPageSize(userVo.getPageSize());
        return MoliResult.success(result);
    }

    /**
     * 添加用户
     *
     * @return 添加用户
     */
    @PostMapping
    public MoliResult<Boolean> insert(@RequestBody UserVo userVo) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userVo, user);
        sysUserMapper.insert(user);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 更新用户
     *
     * @return
     */
    @PutMapping
    public MoliResult<Boolean> update(@RequestBody SysUser user) {
        sysUserMapper.updateById(user);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 查询单个用户
     */
    @GetMapping(value = "/{id}")
    public MoliResult<SysUser> getInfo(@PathVariable Long id) {

        return MoliResult.success(sysUserMapper.selectById(id));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    public MoliResult delete(@PathVariable Long[] userIds) {
        for (Long id : userIds) {
            SysUser user = new SysUser();
            user.setId(id);
            user.setIsDelete(CommonConstant.IS_DELETE);
            sysUserMapper.updateById(user);
            sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
        }

        return MoliResult.success(Boolean.TRUE);
    }

    @PutMapping("/changeStatus")
    public MoliResult changeStatus(@RequestBody SysUser user) {
        sysUserMapper.updateById(user);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 查询单个用户下的角色信息
     */
    @GetMapping(value = "/getRoleByUserId/{userId}")
    public MoliResult<UserRoleVo> getRoleByUserId(@PathVariable Long userId) {
        UserRoleVo userRoleVo = new UserRoleVo();
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (CollectionUtils.isEmpty(userRoleList)) {
            return MoliResult.success(userRoleVo);
        }
        SysUser user = sysUserMapper.selectById(userId);
        userRoleVo.setUser(user);
        List<Long> roleIdList = userRoleList.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
        List<SysRole> roleList = roleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIdList));
        userRoleVo.setRoleList(roleList);
        return MoliResult.success(userRoleVo);
    }

    /**
     * 保存授权角色
     *
     * @return
     */
    @PutMapping("/inserUserRole")
    public MoliResult<Boolean> inserUserRole(@RequestBody UserRoleVo userRoleVo) {
        List<SysUserRole> userRoleList = new ArrayList<>();
        //删除用户角色关系
        userRoleService.remove(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userRoleVo.getUserId()));
        if (CollectionUtils.isNotEmpty(userRoleVo.getRoleIds())) {
            for (Long roleId : userRoleVo.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userRoleVo.getUserId());
                userRole.setRoleId(roleId);
                userRoleList.add(userRole);
            }
            userRoleService.saveBatch(userRoleList);
        }
        return MoliResult.success(Boolean.TRUE);
    }

    @PutMapping("/resetPassword")
    @ApiOperation(value = "重置密码")
    public MoliResult<Boolean> resetPassword(@RequestBody SysUser sysUser) {
        sysUser.setPassword(SHA256Util.sha256(sysUser.getPassword(), SHA256Util.SALT));
        sysUserMapper.updateById(sysUser);
        return MoliResult.success(Boolean.TRUE);
    }

}
