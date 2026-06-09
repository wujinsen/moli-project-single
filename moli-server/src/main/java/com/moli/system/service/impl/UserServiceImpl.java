package com.moli.system.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.moli.common.constant.CommonConstant;

import com.moli.common.domain.entity.SysDept;
import com.moli.common.domain.entity.SysRole;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.entity.SysUserRole;
import com.moli.common.domain.vo.UserVo;
import com.moli.common.page.PageRes;
import com.moli.common.utils.MoliDateUtils;
import com.moli.common.utils.PrivilegedUserUtils;
import com.moli.config.util.ShiroUtils;
import com.moli.system.mapper.PostMapper;
import com.moli.system.mapper.RoleMapper;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.mapper.SysUserRoleMapper;
import com.moli.system.service.DeptService;
import com.moli.system.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service

public class UserServiceImpl implements UserService {



    @Resource

    private SysUserMapper sysUserMapper;



    @Resource

    private DeptService deptService;



    @Resource

    private PostMapper postMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private RoleMapper roleMapper;



    @Override

    public PageRes<UserVo> list(UserVo userVo) {

        PageRes<UserVo> result = new PageRes<>();

        List<UserVo> list = new ArrayList<>();

        SysUser currentUser = resolveCurrentUser();

        String currentUserName = currentUser != null ? currentUser.getUserName() : null;

        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        boolean currentIsPrivileged = PrivilegedUserUtils.isPrivilegedAccount(currentUserName);



        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (userVo.getDeptId() != null) {

            List<Long> deptIdList = new ArrayList<>();

            List<SysDept> deptList = deptService.list();

            deptIdList.add(userVo.getDeptId());

            deptService.findChildrenDeptIdTree(deptIdList, deptList, userVo.getDeptId());

            if (currentIsPrivileged && currentUserId != null) {

                lambdaQueryWrapper.and(w -> w.in(SysUser::getDeptId, deptIdList).or().eq(SysUser::getId, currentUserId));

            } else {

                lambdaQueryWrapper.in(SysUser::getDeptId, deptIdList);

            }

        }

        if (StringUtils.isNotBlank(userVo.getUserName())) {

            lambdaQueryWrapper.eq(SysUser::getUserName, userVo.getUserName());

        }

        if (StringUtils.isNotBlank(userVo.getTelephone())) {

            lambdaQueryWrapper.eq(SysUser::getTelephone, userVo.getTelephone());

        }

        if (userVo.getStatus() != null) {

            lambdaQueryWrapper.eq(SysUser::getStatus, userVo.getStatus());

        }

        if (userVo.getBeginTime() != null) {

            lambdaQueryWrapper.between(SysUser::getCreateTime,

                    MoliDateUtils.startTimeToDateStart(userVo.getBeginTime()),

                    MoliDateUtils.endTimeToDateEnd(userVo.getEndTime()));

        }

        lambdaQueryWrapper.eq(SysUser::getIsDelete, CommonConstant.UN_DELETE);

        applyPrivilegedUserVisibility(lambdaQueryWrapper);



        Page<SysUser> page = new Page<>();

        page.setCurrent(userVo.getPageNum());

        page.setSize(userVo.getPageSize());

        sysUserMapper.selectPage(page, lambdaQueryWrapper);

        if (CollectionUtils.isEmpty(page.getRecords())) {

            result.setTotal((int) page.getTotal());

            result.setList(list);

            result.setPageNum(userVo.getPageNum());

            result.setPageSize(userVo.getPageSize());

            return result;

        }



        List<SysDept> deptList = deptService.list();

        Map<Long, String> mapDeptName = deptList.stream()
                .collect(Collectors.toMap(SysDept::getId, SysDept::getDeptName, (a, b) -> a));

        List<Long> userIds = page.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
        Map<Long, String> userRoleNames = resolveUserRoleNames(userIds);

        page.getRecords().forEach(e -> {
            UserVo userVoTwo = new UserVo();
            BeanUtils.copyProperties(e, userVoTwo);
            if (mapDeptName.get(e.getDeptId()) != null) {
                userVoTwo.setDeptName(mapDeptName.get(e.getDeptId()));
            }
            userVoTwo.setRoleNames(userRoleNames.get(e.getId()));
            list.add(userVoTwo);
        });

        result.setTotal((int) page.getTotal());

        result.setList(list);

        result.setPageNum(userVo.getPageNum());

        result.setPageSize(userVo.getPageSize());

        return result;

    }



    @Override

    public SysUser resolveCurrentUser() {

        SysUser principal = ShiroUtils.getUserInfo();

        if (principal == null) {

            return null;

        }

        if (principal.getId() != null) {

            SysUser fromDb = sysUserMapper.selectById(principal.getId());

            if (fromDb != null) {

                return fromDb;

            }

        }

        return principal;

    }



    @Override

    public void applyPrivilegedUserVisibility(LambdaQueryWrapper<SysUser> wrapper) {

        SysUser currentUser = resolveCurrentUser();

        String currentUserName = currentUser != null ? currentUser.getUserName() : null;

        PrivilegedUserUtils.applyListVisibilityFilter(wrapper, currentUserName);

    }



    @Override
    public boolean canViewUser(SysUser target) {
        return PrivilegedUserUtils.canViewUser(target, resolveCurrentUser());
    }

    private Map<Long, String> resolveUserRoleNames(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userIds));
        if (CollectionUtils.isEmpty(userRoleList)) {
            return Collections.emptyMap();
        }

        List<Long> roleIds = userRoleList.stream().map(SysUserRole::getRoleId).distinct().collect(Collectors.toList());
        Map<Long, String> roleNameMap = roleMapper.selectList(
                        new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds))
                .stream()
                .collect(Collectors.toMap(SysRole::getId, SysRole::getRoleName, (a, b) -> a));

        Map<Long, List<Long>> userRoleIdMap = new HashMap<>();
        for (SysUserRole userRole : userRoleList) {
            userRoleIdMap.computeIfAbsent(userRole.getUserId(), key -> new ArrayList<>()).add(userRole.getRoleId());
        }

        Map<Long, String> result = new HashMap<>();
        for (Map.Entry<Long, List<Long>> entry : userRoleIdMap.entrySet()) {
            String names = entry.getValue().stream()
                    .map(roleNameMap::get)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(", "));
            if (StringUtils.isNotBlank(names)) {
                result.put(entry.getKey(), names);
            }
        }
        return result;
    }

}


