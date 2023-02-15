package com.moli.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.domain.entity.SysDept;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.vo.UserVo;
import com.moli.common.page.PageRes;
import com.moli.common.utils.MoliDateUtils;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.service.DeptService;
import com.moli.system.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl  implements UserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private DeptService deptService;

    @Override
    public PageRes<SysUser> list(UserVo userVo) {
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
            lambdaQueryWrapper.eq(SysUser::getUserName, userVo.getUserName());
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
        return result;
    }

}
