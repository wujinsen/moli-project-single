package com.moli.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moli.common.constant.PermissionConstants;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysDept;
import com.moli.common.domain.vo.DeptVo;
import com.moli.common.domain.vo.PostVo;
import com.moli.system.mapper.DeptMapper;
import com.moli.system.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("dept")
@Api(tags = "部门管理")
@Slf4j
public class DeptController {

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private DeptService deptService;

    @GetMapping("/list")
    @RequiresPermissions(PermissionConstants.SYSTEM_DEPT_LIST)
    @ApiOperation(value = "部门列表", notes = "部门列表")
    public MoliResult<List<DeptVo>> list(SysDept dept) {

        LambdaQueryWrapper<SysDept> lambdaQueryWrapper = new LambdaQueryWrapper();
        List<DeptVo> deptVoList = new ArrayList();
        if (StringUtils.isNotBlank(dept.getDeptName())) {
            lambdaQueryWrapper.like(SysDept::getDeptName, dept.getDeptName());
        }
        if (dept.getStatus() != null) {
            lambdaQueryWrapper.eq(SysDept::getStatus, dept.getStatus());
        }
        List<SysDept> deptList = deptMapper.selectList(lambdaQueryWrapper);
        for (SysDept entity : deptList) {
            DeptVo deptVo = new DeptVo();
            BeanUtils.copyProperties(entity, deptVo);
            deptVoList.add(deptVo);
        }
        return MoliResult.success(deptVoList);
    }


    @GetMapping("/getDeptTreeList")
    @RequiresPermissions(PermissionConstants.SYSTEM_DEPT_LIST)
    @ApiOperation(value = "部门列表", notes = "部门列表")
    public MoliResult<List<DeptVo>> getDeptTreeList() {

        LambdaQueryWrapper<SysDept> lambdaQueryWrapper = new LambdaQueryWrapper();
        List<DeptVo> deptVoList = new ArrayList();
        List<SysDept> deptList = deptMapper.selectList(lambdaQueryWrapper);
        for (SysDept dept : deptList) {
            DeptVo deptVo = new DeptVo();
            BeanUtils.copyProperties(dept, deptVo);
            deptVoList.add(deptVo);
        }

        return MoliResult.success(createTree(deptVoList));
    }


    @PostMapping
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_DEPT_ADD, PermissionConstants.SYSTEM_DEPT_LIST}, logical = Logical.AND)
    @ApiOperation(value = "添加部门", notes = "添加部门")
    public MoliResult<Boolean> insert(@RequestBody SysDept dept) {
        deptMapper.insert(dept);
        return MoliResult.success(Boolean.TRUE);
    }


    @PutMapping
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_DEPT_EDIT, PermissionConstants.SYSTEM_DEPT_LIST}, logical = Logical.AND)
    @ApiOperation(value = "更新部门", notes = "更新部门")
    public MoliResult<Boolean> update(@RequestBody SysDept dept) {
        deptMapper.updateById(dept);
        return MoliResult.success(Boolean.TRUE);
    }

    @GetMapping(value = "/{id}")
    @RequiresPermissions(PermissionConstants.SYSTEM_DEPT_LIST)
    @ApiOperation(value = "查询单个部门", notes = "查询单个部门")
    public MoliResult<SysDept> getInfo(@PathVariable Long id) {

        return MoliResult.success(deptMapper.selectById(id));
    }


    @DeleteMapping("/{id}")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_DEPT_REMOVE, PermissionConstants.SYSTEM_DEPT_LIST}, logical = Logical.AND)
    @ApiOperation(value = "删除单个部门", notes = "删除指定部门，并级联删除其下所有子部门")
    public MoliResult<Boolean> remove(@PathVariable("id") Long id) {
        return MoliResult.success(deptService.deleteWithChildren(id));
    }

    private static List<DeptVo> createTree(List<DeptVo> deptList) {
        List<DeptVo> list = new ArrayList<>();
        for (DeptVo treeNode : deptList) {
            if (treeNode.getParentId().intValue() == 0) {
                list.add(findChildrenTree(treeNode, deptList));
            }
        }
        return list;
    }

    /**
     * 递归查找当前节点下的所有子节点
     *
     * @param htgDeptVo 当前节点
     * @param deptList  所有节点
     */
    private static DeptVo findChildrenTree(DeptVo htgDeptVo, List<DeptVo> deptList) {
        List<DeptVo> childrenList = new ArrayList<>();
        for (DeptVo childrenNode : deptList) {
            if (htgDeptVo.getId().equals(childrenNode.getParentId())) {
                childrenList.add(childrenNode);
            }
        }
        if (CollectionUtils.isNotEmpty(childrenList)) {
            htgDeptVo.setChildren(childrenList);
            for (DeptVo htgMenuVoTwo : childrenList) {
                findChildrenTree(htgMenuVoTwo, deptList);
            }
        } else {
            htgDeptVo.setChildren(new ArrayList<>());
        }
        return htgDeptVo;
    }
}
