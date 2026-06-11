package com.moli.operation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.PermissionConstants;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.OperationComponentDeployInfo;
import com.moli.common.page.PageRes;
import com.moli.operation.mapper.OperationComponentDeployInfoMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operation/component")
@Api(tags = "运维组件管理")
@Slf4j
public class OperationComponentController {

    @Autowired
    private OperationComponentDeployInfoMapper operationComponentDeployInfoMapper;

    @GetMapping("/list")
    @RequiresPermissions(PermissionConstants.OPERATION_COMPONENT_LIST)
    @ApiOperation(value = "组件列表", notes = "组件列表")
    public MoliResult<PageRes<OperationComponentDeployInfo>> list(OperationComponentDeployInfo operationComponentDeployInfo) {
        PageRes<OperationComponentDeployInfo> result = new PageRes<>();
        LambdaQueryWrapper<OperationComponentDeployInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(operationComponentDeployInfo.getComponentName())) {
            lambdaQueryWrapper.like(OperationComponentDeployInfo::getComponentName, operationComponentDeployInfo.getComponentName());
        }
        if (operationComponentDeployInfo.getServerIp() != null) {
            lambdaQueryWrapper.eq(OperationComponentDeployInfo::getServerIp, operationComponentDeployInfo.getServerIp());
        }
        if (operationComponentDeployInfo.getEnvironment() != null) {
            lambdaQueryWrapper.eq(OperationComponentDeployInfo::getEnvironment, operationComponentDeployInfo.getEnvironment());
        }

        lambdaQueryWrapper.orderByDesc(OperationComponentDeployInfo::getCreateTime);
        Page page = new Page();
        page.setCurrent(operationComponentDeployInfo.getPageNum());
        page.setSize(operationComponentDeployInfo.getPageSize());
        operationComponentDeployInfoMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(page.getRecords());
        result.setPageNum(operationComponentDeployInfo.getPageNum());
        result.setPageSize(operationComponentDeployInfo.getPageSize());
        return MoliResult.success(result);

    }

    @PostMapping
    @RequiresPermissions(value = {PermissionConstants.OPERATION_COMPONENT_ADD, PermissionConstants.OPERATION_COMPONENT_LIST}, logical = Logical.AND)
    @ApiOperation(value = "添加组件", notes = "添加组件")
    public MoliResult<Boolean> insert(@RequestBody OperationComponentDeployInfo operationServerInfo) {
        operationComponentDeployInfoMapper.insert(operationServerInfo);
        return MoliResult.success(Boolean.TRUE);
    }


    @PutMapping
    @RequiresPermissions(value = {PermissionConstants.OPERATION_COMPONENT_EDIT, PermissionConstants.OPERATION_COMPONENT_LIST}, logical = Logical.AND)
    @ApiOperation(value = "更新组件", notes = "更新组件")
    public MoliResult<Boolean> update(@RequestBody OperationComponentDeployInfo operationServerInfo) {
        operationComponentDeployInfoMapper.updateById(operationServerInfo);
        return MoliResult.success(Boolean.TRUE);
    }

    @GetMapping(value = "/{id}")
    @RequiresPermissions(PermissionConstants.OPERATION_COMPONENT_LIST)
    @ApiOperation(value = "查询单个组件", notes = "查询单个组件")
    public MoliResult<OperationComponentDeployInfo> selectOne(@PathVariable Long id) {

        return MoliResult.success(operationComponentDeployInfoMapper.selectById(id));
    }

    @DeleteMapping("/{ids}")
    @RequiresPermissions(value = {PermissionConstants.OPERATION_COMPONENT_REMOVE, PermissionConstants.OPERATION_COMPONENT_LIST}, logical = Logical.AND)
    @ApiOperation(value = "删除组件", notes = "删除组件")
    public MoliResult remove(@PathVariable Long[] ids) {
        for (Long id : ids) {
            operationComponentDeployInfoMapper.deleteById(id);
        }
        return MoliResult.success(Boolean.TRUE);
    }
}
