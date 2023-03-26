package com.moli.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.domain.entity.SysDept;
import com.moli.common.domain.entity.SysPost;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.vo.UserVo;
import com.moli.common.page.PageRes;
import com.moli.common.utils.MoliDateUtils;
import com.moli.system.mapper.PostMapper;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.service.DeptService;
import com.moli.system.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
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


    @Override
    public PageRes<UserVo> list(UserVo userVo) {

        PageRes<UserVo> result = new PageRes<>();
        List<UserVo> list = new ArrayList<>();
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (userVo.getDeptId() != null) {
            List<Long> detpIdList = new ArrayList<>();
            List<SysDept> deptList = deptService.list();
            detpIdList.add(userVo.getDeptId());
            deptService.findChildrenDeptIdTree(detpIdList, deptList, userVo.getDeptId());
            lambdaQueryWrapper.in(SysUser::getDeptId, detpIdList);
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
            lambdaQueryWrapper.between(SysUser::getCreateTime, MoliDateUtils.startTimeToDateStart(userVo.getBeginTime()), MoliDateUtils.endTimeToDateEnd(userVo.getEndTime()));
        }
        lambdaQueryWrapper.eq(SysUser::getIsDelete, CommonConstant.UN_DELETE);
        lambdaQueryWrapper.ne(SysUser::getUserName, CommonConstant.SUPER_ADMIN);
        Page<SysUser> page = new Page();
        page.setCurrent(userVo.getPageNum());
        page.setSize(userVo.getPageSize());
        sysUserMapper.selectPage(page, lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            Long total = page.getTotal();
            result.setTotal(total.intValue());
            result.setList(list);
            result.setPageNum(userVo.getPageNum());
            result.setPageSize(userVo.getPageSize());
            return result;
        }

        List<SysDept> deptList = deptService.list();
        Map<Long, String> mapDeptName = deptList.stream().collect(Collectors.toMap(e -> e.getId(), e -> e.getDeptName()));

        page.getRecords().forEach(e -> {
            UserVo userVoTwo = new UserVo();
            BeanUtils.copyProperties(e, userVoTwo);
            if (mapDeptName.get(e.getDeptId()) != null) {
                userVoTwo.setDeptName(mapDeptName.get(e.getDeptId()));
            }
            list.add(userVoTwo);
        });
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(list);
        result.setPageNum(userVo.getPageNum());
        result.setPageSize(userVo.getPageSize());
        return result;
    }

}
