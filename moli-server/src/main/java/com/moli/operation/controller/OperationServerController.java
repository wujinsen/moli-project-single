package com.moli.operation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.PermissionConstants;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.OperationServerInfo;
import com.moli.common.domain.vo.OperationServerInfoVo;
import com.moli.common.page.PageRes;
import com.moli.operation.mapper.OperationServerMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/operation/server")
@Api(tags = "服务器管理")
@Slf4j
public class OperationServerController {

    @Resource
    private OperationServerMapper operationServerMapper;

    @GetMapping("/list")
    @RequiresPermissions(PermissionConstants.OPERATION_SERVER_LIST)
    @ApiOperation(value = "服务器列表", notes = "服务器列表")
    public MoliResult<PageRes<OperationServerInfo>> list(OperationServerInfoVo operationServerInfoVo) {
        PageRes<OperationServerInfo> result = new PageRes<>();
        LambdaQueryWrapper<OperationServerInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(operationServerInfoVo.getServerName())) {
            lambdaQueryWrapper.like(OperationServerInfo::getServerName, operationServerInfoVo.getServerName());
        }
        if (operationServerInfoVo.getIp() != null) {
            lambdaQueryWrapper.eq(OperationServerInfo::getIp, operationServerInfoVo.getIp());
        }
        if (operationServerInfoVo.getEnvironment() != null) {
            lambdaQueryWrapper.eq(OperationServerInfo::getEnvironment, operationServerInfoVo.getEnvironment());
        }

        lambdaQueryWrapper.orderByDesc(OperationServerInfo::getCreateTime);
        Page page = new Page();
        page.setCurrent(operationServerInfoVo.getPageNum());
        page.setSize(operationServerInfoVo.getPageSize());
        operationServerMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(page.getRecords());
        result.setPageNum(operationServerInfoVo.getPageNum());
        result.setPageSize(operationServerInfoVo.getPageSize());
        return MoliResult.success(result);

    }

    @PostMapping
    @RequiresPermissions(value = {PermissionConstants.OPERATION_SERVER_ADD, PermissionConstants.OPERATION_SERVER_LIST}, logical = Logical.AND)
    @ApiOperation(value = "添加服务器", notes = "添加服务器")
    public MoliResult<Boolean> insert(@RequestBody OperationServerInfo operationServerInfo) {
        operationServerMapper.insert(operationServerInfo);
        return MoliResult.success(Boolean.TRUE);
    }


    @PutMapping
    @RequiresPermissions(value = {PermissionConstants.OPERATION_SERVER_EDIT, PermissionConstants.OPERATION_SERVER_LIST}, logical = Logical.AND)
    @ApiOperation(value = "更新服务器", notes = "更新服务器")
    public MoliResult<Boolean> update(@RequestBody OperationServerInfo operationServerInfo) {
        operationServerMapper.updateById(operationServerInfo);
        return MoliResult.success(Boolean.TRUE);
    }

    @GetMapping(value = "/{id}")
    @RequiresPermissions(PermissionConstants.OPERATION_SERVER_LIST)
    @ApiOperation(value = "查询单个服务器", notes = "查询单个服务器")
    public MoliResult<OperationServerInfo> selectOne(@PathVariable Long id) {

        return MoliResult.success(operationServerMapper.selectById(id));
    }

    @DeleteMapping("/{ids}")
    @RequiresPermissions(value = {PermissionConstants.OPERATION_SERVER_REMOVE, PermissionConstants.OPERATION_SERVER_LIST}, logical = Logical.AND)
    @ApiOperation(value = "删除服务器", notes = "删除服务器")
    public MoliResult remove(@PathVariable Long[] ids) {
        for (Long id : ids) {
            operationServerMapper.deleteById(id);
        }
        return MoliResult.success(Boolean.TRUE);
    }
}
