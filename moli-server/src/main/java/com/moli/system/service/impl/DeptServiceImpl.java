package com.moli.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moli.common.domain.entity.SysDept;
import com.moli.system.mapper.DeptMapper;
import com.moli.system.service.DeptService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl  extends ServiceImpl<DeptMapper, SysDept> implements DeptService  {


    /**
     * 根据当前部门获取所有下级部门
     *
     * @param result   返回结果，所有下级部门id
     * @param deptList 所有部门内容
     * @param deptId   当前部门id
     */
    public void findChildrenDeptIdTree(List<Long> result, List<SysDept> deptList, Long deptId) {
        for (SysDept entity : deptList) {
            if (deptId != null && deptId.equals(entity.getParentId())) {
                result.add(entity.getId());
                findChildrenDeptIdTree(result, deptList, entity.getId());
            }
        }
    }

}
