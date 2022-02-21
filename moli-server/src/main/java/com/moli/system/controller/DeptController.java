package com.moli.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.Dept;
import com.moli.common.domain.entity.Role;
import com.moli.common.domain.entity.User;
import com.moli.common.domain.vo.DeptVo;
import com.moli.common.page.PageReq;
import com.moli.common.page.PageRes;
import com.moli.system.mapper.DeptMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("dept")
@Api(tags = "部门管理")
@Slf4j
public class DeptController {

    @Autowired
    private DeptMapper deptMapper;

    /**
     * 部门列表
     *
     * @param req
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "部门列表", notes = "部门列表")
    public MoliResult<List<DeptVo>> list(Dept dept) {

        LambdaQueryWrapper<Dept> lambdaQueryWrapper = new LambdaQueryWrapper();
        List<DeptVo> deptVoList = new ArrayList();
        if (StringUtils.isNotBlank(dept.getDeptName())) {
            lambdaQueryWrapper.like(Dept::getDeptName, dept.getDeptName());
        }
        List<Dept> deptList = deptMapper.selectList(lambdaQueryWrapper);
        for (Dept entity : deptList) {
            DeptVo deptVo = new DeptVo();
            BeanUtils.copyProperties(entity, deptVo);
            deptVoList.add(deptVo);
        }
        return MoliResult.success(deptVoList);
    }

    /**
     * 部门列表
     *
     * @param
     * @return
     */
    @GetMapping("/getDeptTreeList")
    @ApiOperation(value = "部门列表", notes = "部门列表")
    public MoliResult<List<DeptVo>> getDeptTreeList() {

        LambdaQueryWrapper<Dept> lambdaQueryWrapper = new LambdaQueryWrapper();
        List<DeptVo> deptVoList = new ArrayList();
        List<Dept> deptList = deptMapper.selectList(lambdaQueryWrapper);
        for (Dept dept : deptList) {
            DeptVo deptVo = new DeptVo();
            BeanUtils.copyProperties(dept, deptVo);
            deptVoList.add(deptVo);
        }

        return MoliResult.success(createTree(deptVoList));
    }

    /**
     * 添加用户
     *
     * @return 添加用户
     */
    @PostMapping
    public MoliResult<Boolean> insert(@RequestBody Dept dept) {
        deptMapper.insert(dept);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 更新用户
     *
     * @return
     */
    @PutMapping
    public MoliResult<Boolean> update(@RequestBody Dept dept) {
        deptMapper.updateById(dept);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 查询单个用户
     */
    @GetMapping(value = "/{id}")
    public MoliResult<Dept> getInfo(@PathVariable Long id) {

        return MoliResult.success(deptMapper.selectById(id));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public MoliResult remove(@PathVariable("id") Long id) {
        deptMapper.deleteById(id);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 递归查询一级节点
     */
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
