package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.*;
import com.moli.common.domain.vo.SysUserVo;
import com.moli.common.domain.vo.UserRoleVo;
import com.moli.common.domain.vo.UserVo;
import com.moli.common.page.PageRes;
import com.moli.config.util.SHA256Util;
import com.moli.config.util.ShiroUtils;
import com.moli.system.mapper.*;
import com.moli.system.service.UserRoleService;
import com.moli.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
    private UserService userService;

    @Autowired
    private UserPostMapper userPostMapper;

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private DeptMapper deptMapper;

    @GetMapping("/list")
    @ApiOperation(value = "用户列表", notes = "用户列表")
    @RequiresPermissions("sys:user:info")
    public MoliResult<PageRes<UserVo>> list(UserVo userVo) {

        return MoliResult.success(userService.list(userVo));
    }

    @PostMapping
    @ApiOperation(value = "添加用户", notes = "添加用户")
    public MoliResult<Boolean> insert(@RequestBody UserVo userVo) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userVo, user);
        user.setSalt(SHA256Util.SALT);
        sysUserMapper.insert(user);
        return MoliResult.success(Boolean.TRUE);
    }


    @PutMapping
    @ApiOperation(value = "更新用户", notes = "更新用户")
    public MoliResult<Boolean> update(@RequestBody SysUserVo req) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(req, sysUser);
        sysUserMapper.updateById(sysUser);
        if(CollectionUtils.isNotEmpty(req.getPostIds())) {
            userPostMapper.delete(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, req.getId()));
            for (Long postId : req.getPostIds()) {
                SysUserPost sysUserPost = new SysUserPost();
                sysUserPost.setUserId(req.getId());
                sysUserPost.setPostId(postId);
                userPostMapper.insert(sysUserPost);
            }
        }
        return MoliResult.success(Boolean.TRUE);
    }


    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询单个用户", notes = "查询单个用户")
    public MoliResult<SysUser> getInfo(@PathVariable Long id) {
        return MoliResult.success(sysUserMapper.selectById(id));
    }


    @GetMapping(value = "/getUserDetail/{id}")
    @ApiOperation(value = "获取用户详情", notes = "获取用户详情")
    public MoliResult<SysUserVo> getUserDetail(@PathVariable Long id) {
        SysUserVo sysUserVo = new SysUserVo();
        SysUser sysUser = sysUserMapper.selectById(id);
        BeanUtils.copyProperties(sysUser, sysUserVo);
        List<SysUserPost> userPostList = userPostMapper.selectList(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, id));
        List<Long> postIds = userPostList.stream().map(e -> e.getPostId()).collect(Collectors.toList());
        sysUserVo.setPostIds(postIds);
        return MoliResult.success(sysUserVo);
    }

    @GetMapping(value = "/profile")
    @ApiOperation(value = "获取用户详情", notes = "获取用户详情")
    public MoliResult<SysUserVo> getUserProfile() {
        SysUserVo sysUserVo = new SysUserVo();
        SysUser sysUser = ShiroUtils.getUserInfo();
        SysUser user = sysUserMapper.selectById(sysUser.getId());
        BeanUtils.copyProperties(user, sysUserVo);
        SysDept sysDept = deptMapper.selectById(sysUser.getDeptId());
        sysUserVo.setDeptName(sysDept.getDeptName());
        List<SysUserPost> userPostList = userPostMapper.selectList(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, user.getId()));
        if(CollectionUtils.isNotEmpty(userPostList)){
            List<Long> postIdList = userPostList.stream().map(e->e.getPostId()).collect(Collectors.toList());
            List<SysPost> postList = postMapper.selectList(new LambdaQueryWrapper<SysPost>().in(SysPost::getId, postIdList));
            sysUserVo.setPostNames(String.join(",",postList.stream().map(e->e.getPostName()).collect(Collectors.toList())));
        }
        return MoliResult.success(sysUserVo);
    }


    @DeleteMapping("/{userIds}")
    @ApiOperation(value = "删除用户", notes = "删除用户")
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
    @ApiOperation(value = "开启关闭用户", notes = "开启关闭用户")
    public MoliResult changeStatus(@RequestBody SysUser user) {
        sysUserMapper.updateById(user);
        return MoliResult.success(Boolean.TRUE);
    }


    @GetMapping(value = "/getRoleByUserId/{userId}")
    @ApiOperation(value = "查询单个用户下的角色信息", notes = "查询单个用户下的角色信息")
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

    @PutMapping("/insertUserRole")
    @ApiOperation(value = "保存授权角色", notes = "保存授权角色")
    public MoliResult<Boolean> insertUserRole(@RequestBody UserRoleVo userRoleVo) {
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

    @PutMapping("/addUserRole")
    @ApiOperation(value = "给角色新增用户", notes = "给角色新增用户")
    public MoliResult<Boolean> addUserRole(@RequestBody UserRoleVo userRoleVo) {
        List<SysUserRole> userRoleList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userRoleVo.getUserIds())) {
            for (Long userId : userRoleVo.getUserIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(userRoleVo.getRoleId());
                userRoleList.add(userRole);
            }
            userRoleService.saveBatch(userRoleList);
        }
        return MoliResult.success(Boolean.TRUE);
    }

    @GetMapping("/getUserByRole")
    @ApiOperation(value = "查询角色下的用户", notes = "查询角色下的用户")
    public MoliResult<PageRes<SysUser>> getUserByRole(UserVo req) {
        PageRes<SysUser> result = new PageRes<>();
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, req.getRoleId()));
        if (CollectionUtils.isEmpty(userRoleList)) {
            return MoliResult.success();
        }
        List<Long> userIdList = userRoleList.stream().map(e -> e.getUserId()).collect(Collectors.toList());

        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(req.getUserName())) {
            lambdaQueryWrapper.like(SysUser::getUserName, req.getUserName());
        }
        if (StringUtils.isNotBlank(req.getTelephone())) {
            lambdaQueryWrapper.like(SysUser::getTelephone, req.getTelephone());
        }
        lambdaQueryWrapper.in(SysUser::getId, userIdList);
        Page page = new Page();
        page.setCurrent(req.getPageNum());
        page.setSize(req.getPageSize());
        sysUserMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(page.getRecords());
        result.setPageNum(req.getPageNum());
        result.setPageSize(req.getPageSize());
        return MoliResult.success(result);
    }

    @PutMapping("/removeUsers")
    @ApiOperation(value = "批量移除角色下的用户", notes = "批量移除角色下的用户")
    public MoliResult removeUsers(@RequestBody UserRoleVo req) {
        if (CollectionUtils.isNotEmpty(req.getUserIds())) {
            for (Long userId : req.getUserIds()) {
                sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, req.getRoleId()).eq(SysUserRole::getUserId, userId));
            }
        }
        return MoliResult.success();
    }

    @GetMapping("/unauthorizedUsers")
    @ApiOperation(value = "查询角色未授权用户列表", notes = "查询角色未授权用户列表")
    public MoliResult<PageRes<SysUser>> unauthorizedUsers(UserVo req) {
        PageRes<SysUser> result = new PageRes<>();
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper();
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, req.getRoleId()));
        if (CollectionUtils.isNotEmpty(userRoleList)) {
            req.setUserIds(userRoleList.stream().map(e -> e.getUserId()).collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(req.getUserIds())) {
            lambdaQueryWrapper.notIn(SysUser::getId, req.getUserIds());
        }
        lambdaQueryWrapper.eq(SysUser::getIsDelete, CommonConstant.UN_DELETE);
        Page page = new Page();
        page.setCurrent(req.getPageNum());
        page.setSize(req.getPageSize());
        sysUserMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(page.getRecords());
        result.setPageNum(req.getPageNum());
        result.setPageSize(req.getPageSize());

        return MoliResult.success(result);
    }

    @PutMapping("/resetPassword")
    @ApiOperation(value = "重置密码")
    public MoliResult<Boolean> resetPassword(@RequestBody SysUser sysUser) {
        sysUser.setPassword(SHA256Util.sha256(sysUser.getPassword(), SHA256Util.SALT));
        sysUserMapper.updateById(sysUser);
        return MoliResult.success(Boolean.TRUE);
    }

}
