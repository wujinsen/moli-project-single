package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.Role;
import com.moli.common.domain.entity.User;
import com.moli.common.domain.entity.UserRole;
import com.moli.common.domain.vo.UserRoleVo;
import com.moli.common.domain.vo.UserVo;
import com.moli.common.page.PageRes;
import com.moli.system.mapper.RoleMapper;
import com.moli.system.mapper.UserMapper;
import com.moli.system.mapper.UserRoleMapper;
import com.moli.system.service.UserRoleService;
import com.moli.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 用户列表
     *
     * @param userVo
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "用户列表", notes = "用户列表")
    public MoliResult<PageRes<User>> list(UserVo userVo) {
        PageRes<User> result = new PageRes<>();
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (userVo.getDeptId() != null) {
            lambdaQueryWrapper.eq(User::getDeptId, userVo.getDeptId());
        }
        if (StringUtils.isNotBlank(userVo.getUserName())) {
            lambdaQueryWrapper.eq(User::getUserName, userVo.getUserName());
        }
        if (StringUtils.isNotBlank(userVo.getTelephone())) {
            lambdaQueryWrapper.eq(User::getTelephone, userVo.getTelephone());
        }
        if (userVo.getStatus() != null) {
            lambdaQueryWrapper.eq(User::getStatus, userVo.getStatus());
        }
        if (userVo.getBeginTime() != null) {
            lambdaQueryWrapper.between(User::getCreateTime, userVo.getBeginTime() + " 00:00:00", userVo.getEndTime() + " 23:59:59");
        }
        lambdaQueryWrapper.eq(User::getIsDelete, CommonConstant.UN_DELETE);
        Page page = new Page();
        userMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setItems(page.getRecords());
        return MoliResult.success(result);
    }

    /**
     * 添加用户
     *
     * @return 添加用户
     */
    @PostMapping
    public MoliResult<Boolean> insert(@RequestBody UserVo userVo) {
        User user = new User();
        BeanUtils.copyProperties(userVo, user);
        userMapper.insert(user);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 更新用户
     *
     * @return
     */
    @PutMapping
    public MoliResult<Boolean> update(@RequestBody User user) {
        userMapper.updateById(user);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 查询单个用户
     */
    @GetMapping(value = "/{id}")
    public MoliResult<User> getInfo(@PathVariable Long id) {

        return MoliResult.success(userMapper.selectById(id));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    public MoliResult remove(@PathVariable Long[] userIds) {
        for (Long id : userIds) {
            User user = new User();
            user.setId(id);
            user.setIsDelete(CommonConstant.IS_DELETE);
            userMapper.updateById(user);
        }

        return MoliResult.success(Boolean.TRUE);
    }

    @PutMapping("/changeStatus")
    public MoliResult changeStatus(@RequestBody User user) {
        userMapper.updateById(user);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 查询单个用户下的角色信息
     */
    @GetMapping(value = "/getRoleByUserId/{userId}")
    public MoliResult<UserRoleVo> getRoleByUserId(@PathVariable Long userId) {
        UserRoleVo userRoleVo = new UserRoleVo();
        User user = userMapper.selectById(userId);
        userRoleVo.setUser(user);
        List<UserRole> userRoleList = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        List<Long> roleIdList = userRoleList.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
        List<Role> roleList = roleMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getId, roleIdList));
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
        List<UserRole> userRoleList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userRoleVo.getRoleList())) {
            for (Role role : userRoleVo.getRoleList()) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userRoleVo.getUserId());
                userRole.setRoleId(role.getId());
                userRoleList.add(userRole);
            }
            userRoleService.saveBatch(userRoleList);
        }
        return MoliResult.success(Boolean.TRUE);
    }

}
