package com.moli.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moli.common.domain.entity.SysDept;

import java.util.List;

public interface DeptService extends IService<SysDept> {

    void findChildrenDeptIdTree(List<Long> result, List<SysDept> deptList, Long deptId);

}
