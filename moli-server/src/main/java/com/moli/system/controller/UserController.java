package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.constant.PermissionConstants;
import com.moli.common.core.MoliResult;
import com.moli.common.enums.ResponseCodeEnums;
import com.moli.common.domain.entity.*;
import com.moli.common.domain.vo.SysUserVo;
import com.moli.common.domain.vo.UserRoleVo;
import com.moli.common.domain.vo.UserSystemVo;
import com.moli.common.domain.vo.UserVo;
import com.moli.common.constant.SystemConstant;
import com.moli.common.enums.BusinessTypeEnum;
import com.moli.common.log.MoliLog;
import com.moli.common.page.PageRes;
import com.moli.config.util.PermissionAuthUtils;
import com.moli.config.util.SHA256Util;
import com.moli.config.util.ShiroUtils;
import com.moli.common.utils.I18nUtils;
import com.moli.system.mapper.*;
import com.moli.system.service.SysSystemService;
import com.moli.system.service.UserRoleService;
import com.moli.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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

    @Autowired
    private SysSystemService sysSystemService;

    @Autowired
    private SysSystemMapper sysSystemMapper;

    @Autowired
    private SysUserSystemMapper sysUserSystemMapper;

    @GetMapping("/list")
    @RequiresPermissions(PermissionConstants.SYSTEM_USER_LIST)
    @ApiOperation(value = "用户列表", notes = "用户列表；superadmin/admin 为特殊账号，仅特殊账号登录时可见")
    public MoliResult<PageRes<UserVo>> list(UserVo userVo) {

        return MoliResult.success(userService.list(userVo));
    }

    @PostMapping
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_USER_ADD, PermissionConstants.SYSTEM_USER_LIST}, logical = Logical.AND)
    @ApiOperation(value = "添加用户", notes = "添加用户")
    public MoliResult<Boolean> insert(@RequestBody UserVo userVo) {
        if (StringUtils.isBlank(userVo.getPassword())) {
            return MoliResult.errorMsg(ResponseCodeEnums.ERROR.getCode(), "密码不能为空");
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userVo, user);
        user.setSalt(SHA256Util.SALT);
        user.setPassword(SHA256Util.sha256(user.getPassword(), SHA256Util.SALT));
        if (user.getStatus() == null) {
            user.setStatus(CommonConstant.YES);
        }
        sysUserMapper.insert(user);
        return MoliResult.success(Boolean.TRUE);
    }


    @PutMapping
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_USER_EDIT, PermissionConstants.SYSTEM_USER_LIST}, logical = Logical.AND)
    @ApiOperation(value = "更新用户", notes = "更新用户")
    public MoliResult<Boolean> update(@RequestBody SysUserVo req) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(req, sysUser);
        String rawPassword = sysUser.getPassword();
        sysUser.setPassword(null);
        if (StringUtils.isNotBlank(rawPassword)) {
            sysUser.setSalt(SHA256Util.SALT);
            sysUser.setPassword(SHA256Util.sha256(rawPassword, SHA256Util.SALT));
        }
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
    @RequiresPermissions(PermissionConstants.SYSTEM_USER_LIST)
    @ApiOperation(value = "查询单个用户", notes = "查询单个用户")
    public MoliResult<SysUser> getInfo(@PathVariable Long id) {
        SysUser target = sysUserMapper.selectById(id);
        if (!userService.canViewUser(target)) {
            return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限查看该用户");
        }
        return MoliResult.success(target);
    }


    @GetMapping(value = "/getUserDetail/{id}")
    @RequiresPermissions(PermissionConstants.SYSTEM_USER_LIST)
    @ApiOperation(value = "获取用户详情", notes = "获取用户详情")
    public MoliResult<SysUserVo> getUserDetail(@PathVariable Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (!userService.canViewUser(sysUser)) {
            return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限查看该用户");
        }
        SysUserVo sysUserVo = new SysUserVo();
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
        if(sysDept != null){
            sysUserVo.setDeptName(sysDept.getDeptName());
        }
        List<SysUserPost> userPostList = userPostMapper.selectList(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, user.getId()));
        if(CollectionUtils.isNotEmpty(userPostList)){
            List<Long> postIdList = userPostList.stream().map(e->e.getPostId()).collect(Collectors.toList());
            List<SysPost> postList = postMapper.selectList(new LambdaQueryWrapper<SysPost>().in(SysPost::getId, postIdList));
            sysUserVo.setPostNames(String.join(",",postList.stream().map(e->e.getPostName()).collect(Collectors.toList())));
        }
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
        if (CollectionUtils.isNotEmpty(userRoleList)) {
            List<Long> roleIdList = userRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            List<SysRole> roleList = roleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIdList));
            sysUserVo.setRoleList(roleList);
            sysUserVo.setRoleNames(roleList.stream().map(SysRole::getRoleName).filter(StringUtils::isNotBlank).collect(Collectors.joining(", ")));
        }
        return MoliResult.success(sysUserVo);
    }

    @PutMapping("/language")
    @ApiOperation(value = "更新界面语言", notes = "更新界面语言")
    public MoliResult<Boolean> updateLanguage(@RequestBody SysUser req) {
        if (!I18nUtils.isSupported(req.getLanguage())) {
            return MoliResult.errorMsg(com.moli.common.enums.ResponseCodeEnums.ERROR.getCode(), "unsupported language");
        }
        SysUser user = new SysUser();
        user.setId(ShiroUtils.getUserInfo().getId());
        user.setLanguage(req.getLanguage());
        sysUserMapper.updateById(user);
        return MoliResult.success(Boolean.TRUE);
    }


    @DeleteMapping("/{userIds}")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_USER_REMOVE, PermissionConstants.SYSTEM_USER_LIST}, logical = Logical.AND)
    @ApiOperation(value = "删除用户", notes = "删除用户")
    public MoliResult delete(@PathVariable Long[] userIds) {
        for (Long id : userIds) {
            SysUser target = sysUserMapper.selectById(id);
            if (!userService.canViewUser(target)) {
                return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限操作该用户");
            }
            SysUser user = new SysUser();
            user.setId(id);
            user.setIsDelete(CommonConstant.IS_DELETE);
            sysUserMapper.updateById(user);
            sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
            sysUserSystemMapper.delete(new LambdaQueryWrapper<SysUserSystem>().eq(SysUserSystem::getUserId, id));
        }

        return MoliResult.success(Boolean.TRUE);
    }

    @PutMapping("/changeStatus")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_USER_EDIT, PermissionConstants.SYSTEM_USER_LIST}, logical = Logical.AND)
    @ApiOperation(value = "开启关闭用户", notes = "开启关闭用户")
    public MoliResult changeStatus(@RequestBody SysUser user) {
        SysUser target = sysUserMapper.selectById(user.getId());
        if (!userService.canViewUser(target)) {
            return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限操作该用户");
        }
        sysUserMapper.updateById(user);
        if (target != null && user.getStatus() != null && user.getStatus() == 0) {
            ShiroUtils.deleteCache(target.getUserName(), true);
            PermissionAuthUtils.clearUserAuthorizationCache(target.getUserName());
        }
        return MoliResult.success(Boolean.TRUE);
    }


    @GetMapping(value = "/getRoleByUserId/{userId}")
    @RequiresPermissions(PermissionConstants.SYSTEM_USER_LIST)
    @ApiOperation(value = "查询单个用户下的角色信息", notes = "查询单个用户下的角色信息")
    public MoliResult<UserRoleVo> getRoleByUserId(@PathVariable Long userId) {
        UserRoleVo userRoleVo = new UserRoleVo();
        SysUser user = sysUserMapper.selectById(userId);
        if (!userService.canViewUser(user)) {
            return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限查看该用户");
        }
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (CollectionUtils.isEmpty(userRoleList)) {
            return MoliResult.success(userRoleVo);
        }
        userRoleVo.setUser(user);
        List<Long> roleIdList = userRoleList.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
        List<SysRole> roleList = roleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIdList));
        userRoleVo.setRoleList(roleList);
        return MoliResult.success(userRoleVo);
    }

    @PutMapping("/insertUserRole")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_USER_ASSIGN_ROLE, PermissionConstants.SYSTEM_USER_LIST}, logical = Logical.AND)
    @MoliLog(title = "用户角色授权", businessType = BusinessTypeEnum.UPDATE)
    @ApiOperation(value = "保存授权角色", notes = "覆盖保存：会清除该用户原有角色后再写入 roleIds")
    public MoliResult<Boolean> insertUserRole(@RequestBody UserRoleVo userRoleVo) {
        Long userId = resolveUserId(userRoleVo);
        if (userId == null) {
            return MoliResult.errorMsg(ResponseCodeEnums.ERROR.getCode(), "用户ID不能为空");
        }
        userRoleService.remove(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (CollectionUtils.isNotEmpty(userRoleVo.getRoleIds())) {
            List<SysUserRole> userRoleList = new ArrayList<>();
            for (Long roleId : userRoleVo.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleList.add(userRole);
            }
            userRoleService.saveBatch(userRoleList);
        }
        clearAuthorizationCache(userId);
        return MoliResult.success(Boolean.TRUE, PermissionConstants.ROLE_ASSIGN_REFRESH_MSG);
    }

    @GetMapping("/getSystemByUserId/{userId}")
    @RequiresPermissions(PermissionConstants.SYSTEM_USER_LIST)
    @ApiOperation(value = "查询用户可访问的系统", notes = "系统准入（能进哪些系统），与 insertUserRole（本系统内角色）分开配置")
    public MoliResult<UserSystemVo> getSystemByUserId(@PathVariable Long userId) {
        UserSystemVo userSystemVo = new UserSystemVo();
        SysUser user = sysUserMapper.selectById(userId);
        if (!userService.canViewUser(user)) {
            return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限查看该用户");
        }
        userSystemVo.setUserId(userId);
        userSystemVo.setUser(user);
        userSystemVo.setSystemIds(sysSystemService.listSystemIdsByUserId(userId));
        LambdaQueryWrapper<SysSystem> systemWrapper = new LambdaQueryWrapper<SysSystem>()
                .orderByAsc(SysSystem::getSort)
                .orderByAsc(SysSystem::getId);
        if (user == null || !CommonConstant.hasFullPermission(user.getUserName())) {
            systemWrapper.eq(SysSystem::getStatus, SystemConstant.STATUS_ENABLED);
        }
        userSystemVo.setSystemList(sysSystemMapper.selectList(systemWrapper));
        return MoliResult.success(userSystemVo);
    }

    @PutMapping("/insertUserSystem")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_USER_ASSIGN_SYSTEM, PermissionConstants.SYSTEM_USER_LIST}, logical = Logical.AND)
    @MoliLog(title = "用户系统授权", businessType = BusinessTypeEnum.UPDATE)
    @ApiOperation(value = "保存用户可访问系统", notes = "moli-admin 配置系统准入；本系统内权限用 insertUserRole")
    public MoliResult<Boolean> insertUserSystem(@RequestBody UserSystemVo userSystemVo) {
        SysUser user = sysUserMapper.selectById(userSystemVo.getUserId());
        if (!userService.canViewUser(user)) {
            return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限操作该用户");
        }
        if (user != null && CommonConstant.hasFullPermission(user.getUserName())) {
            return MoliResult.success(Boolean.TRUE, "超管账号默认可访问全部系统并拥有最大权限，无需分配");
        }
        sysSystemService.assignUserSystems(userSystemVo.getUserId(), userSystemVo.getSystemIds());
        return MoliResult.success(Boolean.TRUE);
    }

    @GetMapping("/getUserBySystem")
    @RequiresPermissions(PermissionConstants.SYSTEM_USER_LIST)
    @ApiOperation(value = "查询系统下已授权用户", notes = "含 sys_user_system 关联用户及超管账号；分页参数同 /user/list")
    public MoliResult<PageRes<SysUser>> getUserBySystem(UserVo req) {
        if (req.getSystemId() == null) {
            return MoliResult.errorMsg(ResponseCodeEnums.PARAMS_EMPTY_ERROR_CODE.getCode(), "systemId 不能为空");
        }
        if (sysSystemMapper.selectById(req.getSystemId()) == null) {
            return MoliResult.errorMsg(ResponseCodeEnums.PARAMS_ERROR_CODE.getCode(), "系统不存在");
        }
        List<Long> userIdList = sysSystemService.listUserIdsBySystemId(req.getSystemId());
        if (CollectionUtils.isEmpty(userIdList)) {
            return MoliResult.success(emptyUserPage(req));
        }
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(req.getUserName())) {
            lambdaQueryWrapper.like(SysUser::getUserName, req.getUserName());
        }
        if (StringUtils.isNotBlank(req.getTelephone())) {
            lambdaQueryWrapper.like(SysUser::getTelephone, req.getTelephone());
        }
        lambdaQueryWrapper.in(SysUser::getId, userIdList);
        lambdaQueryWrapper.eq(SysUser::getIsDelete, CommonConstant.UN_DELETE);
        userService.applyPrivilegedUserVisibility(lambdaQueryWrapper);
        return MoliResult.success(selectUserPage(req, lambdaQueryWrapper));
    }

    @GetMapping("/unauthorizedUsersBySystem")
    @RequiresPermissions(PermissionConstants.SYSTEM_USER_LIST)
    @ApiOperation(value = "查询系统未授权用户列表", notes = "排除已关联用户及超管账号")
    public MoliResult<PageRes<SysUser>> unauthorizedUsersBySystem(UserVo req) {
        if (req.getSystemId() == null) {
            return MoliResult.errorMsg(ResponseCodeEnums.PARAMS_EMPTY_ERROR_CODE.getCode(), "systemId 不能为空");
        }
        if (sysSystemMapper.selectById(req.getSystemId()) == null) {
            return MoliResult.errorMsg(ResponseCodeEnums.PARAMS_ERROR_CODE.getCode(), "系统不存在");
        }
        List<Long> assignedUserIds = sysSystemService.listUserIdsBySystemId(req.getSystemId());
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(assignedUserIds)) {
            lambdaQueryWrapper.notIn(SysUser::getId, assignedUserIds);
        }
        if (StringUtils.isNotBlank(req.getUserName())) {
            lambdaQueryWrapper.like(SysUser::getUserName, req.getUserName());
        }
        if (StringUtils.isNotBlank(req.getTelephone())) {
            lambdaQueryWrapper.like(SysUser::getTelephone, req.getTelephone());
        }
        lambdaQueryWrapper.eq(SysUser::getIsDelete, CommonConstant.UN_DELETE);
        userService.applyPrivilegedUserVisibility(lambdaQueryWrapper);
        return MoliResult.success(selectUserPage(req, lambdaQueryWrapper));
    }

    @PutMapping("/addUserRole")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_ROLE_EDIT, PermissionConstants.SYSTEM_ROLE_LIST}, logical = Logical.AND)
    @MoliLog(title = "角色批量授权用户", businessType = BusinessTypeEnum.UPDATE)
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
            clearAuthorizationCache(userRoleVo.getUserIds());
        }
        return MoliResult.success(Boolean.TRUE, PermissionConstants.ROLE_ASSIGN_REFRESH_MSG);
    }

    @GetMapping("/getUserByRole")
    @RequiresPermissions(PermissionConstants.SYSTEM_ROLE_LIST)
    @ApiOperation(value = "查询角色下的用户", notes = "查询角色下的用户")
    public MoliResult<PageRes<SysUser>> getUserByRole(UserVo req) {
        PageRes<SysUser> result = new PageRes<>();
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, req.getRoleId()));
        if (CollectionUtils.isEmpty(userRoleList)) {
            result.setList(Collections.emptyList());
            result.setTotal(0);
            result.setPageNum(req.getPageNum());
            result.setPageSize(req.getPageSize());
            return MoliResult.success(result);
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
        userService.applyPrivilegedUserVisibility(lambdaQueryWrapper);
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
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_ROLE_EDIT, PermissionConstants.SYSTEM_ROLE_LIST}, logical = Logical.AND)
    @MoliLog(title = "角色移除用户", businessType = BusinessTypeEnum.UPDATE)
    @ApiOperation(value = "批量移除角色下的用户", notes = "批量移除角色下的用户")
    public MoliResult removeUsers(@RequestBody UserRoleVo req) {
        if (CollectionUtils.isNotEmpty(req.getUserIds())) {
            for (Long userId : req.getUserIds()) {
                sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, req.getRoleId()).eq(SysUserRole::getUserId, userId));
            }
            clearAuthorizationCache(req.getUserIds());
        }
        return MoliResult.success(Boolean.TRUE, PermissionConstants.ROLE_ASSIGN_REFRESH_MSG);
    }

    @GetMapping("/unauthorizedUsers")
    @RequiresPermissions(PermissionConstants.SYSTEM_ROLE_LIST)
    @ApiOperation(value = "查询角色未授权用户列表", notes = "查询角色未授权用户列表")
    public MoliResult<PageRes<SysUser>> unauthorizedUsers(UserVo req) {
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, req.getRoleId()));
        if (CollectionUtils.isNotEmpty(userRoleList)) {
            req.setUserIds(userRoleList.stream().map(e -> e.getUserId()).collect(Collectors.toList()));
        }

        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (CollectionUtils.isNotEmpty(req.getUserIds())) {
            lambdaQueryWrapper.notIn(SysUser::getId, req.getUserIds());
        }
        if (StringUtils.isNotBlank(req.getUserName())) {
            lambdaQueryWrapper.like(SysUser::getUserName, req.getUserName());
        }
        if (StringUtils.isNotBlank(req.getTelephone())) {
            lambdaQueryWrapper.like(SysUser::getTelephone, req.getTelephone());
        }
        lambdaQueryWrapper.eq(SysUser::getIsDelete, CommonConstant.UN_DELETE);
        userService.applyPrivilegedUserVisibility(lambdaQueryWrapper);
        return MoliResult.success(selectUserPage(req, lambdaQueryWrapper));
    }

    @PutMapping("/resetPassword")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_USER_RESET_PWD, PermissionConstants.SYSTEM_USER_LIST}, logical = Logical.AND)
    @ApiOperation(value = "重置密码")
    public MoliResult<Boolean> resetPassword(@RequestBody SysUser sysUser) {
        SysUser target = sysUserMapper.selectById(sysUser.getId());
        if (!userService.canViewUser(target)) {
            return MoliResult.errorMsg(ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "无权限操作该用户");
        }
        sysUser.setPassword(SHA256Util.sha256(sysUser.getPassword(), SHA256Util.SALT));
        sysUserMapper.updateById(sysUser);
        return MoliResult.success(Boolean.TRUE);
    }

    private void clearAuthorizationCache(Long userId) {
        if (userId == null) {
            return;
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user != null) {
            PermissionAuthUtils.clearUserAuthorizationCache(user.getUserName());
        }
    }

    private void clearAuthorizationCache(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        for (Long userId : userIds) {
            clearAuthorizationCache(userId);
        }
    }

    private Long resolveUserId(UserRoleVo userRoleVo) {
        if (userRoleVo == null) {
            return null;
        }
        if (userRoleVo.getUserId() != null) {
            return userRoleVo.getUserId();
        }
        if (userRoleVo.getUser() != null) {
            return userRoleVo.getUser().getId();
        }
        return null;
    }

    private PageRes<SysUser> emptyUserPage(UserVo req) {
        PageRes<SysUser> result = new PageRes<>();
        result.setTotal(0);
        result.setList(Collections.emptyList());
        result.setPageNum(req.getPageNum());
        result.setPageSize(req.getPageSize());
        return result;
    }

    private PageRes<SysUser> selectUserPage(UserVo req, LambdaQueryWrapper<SysUser> lambdaQueryWrapper) {
        PageRes<SysUser> result = new PageRes<>();
        Page page = new Page();
        page.setCurrent(req.getPageNum());
        page.setSize(req.getPageSize());
        sysUserMapper.selectPage(page, lambdaQueryWrapper);
        result.setTotal((int) page.getTotal());
        result.setList(page.getRecords());
        result.setPageNum(req.getPageNum());
        result.setPageSize(req.getPageSize());
        return result;
    }

}
