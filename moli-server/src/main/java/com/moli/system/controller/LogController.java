package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.PermissionConstants;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysLoginLog;
import com.moli.common.domain.entity.SysOperationLog;
import com.moli.common.page.PageRes;
import com.moli.system.mapper.SysLoginLogMapper;
import com.moli.system.mapper.SysOperationLogMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(tags = "系统操作日志管理")
@RequestMapping("/log")
public class LogController {

    @Autowired
    private SysLoginLogMapper sysLoginLogMapper;

    @Autowired
    private SysOperationLogMapper sysOperationLogMapper;

    @GetMapping("/loginLogList")
    @RequiresPermissions(PermissionConstants.SYSTEM_LOGINLOG_LIST)
    @ApiOperation(value = "登录日志列表")
    public MoliResult<PageRes<SysLoginLog>> loginLogList(SysLoginLog sysLoginLog) {
        PageRes<SysLoginLog> result = new PageRes<>();

        Page<SysLoginLog> page = new Page<>(sysLoginLog.getPageNum(), sysLoginLog.getPageSize());
        LambdaQueryWrapper<SysLoginLog> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(sysLoginLog.getUserName())) {
            lambdaQueryWrapper.like(SysLoginLog::getUserName, sysLoginLog.getUserName());
        }
        if (sysLoginLog.getStatus() != null) {
            lambdaQueryWrapper.eq(SysLoginLog::getStatus, sysLoginLog.getStatus());
        }
        lambdaQueryWrapper.orderByDesc(SysLoginLog::getLoginTime);
        sysLoginLogMapper.selectPage(page, lambdaQueryWrapper);
        result.setList(page.getRecords());
        result.setPageNum(sysLoginLog.getPageNum());
        result.setPageSize(sysLoginLog.getPageSize());
        result.setTotal((int) page.getTotal());
        return MoliResult.success(result);

    }

    @DeleteMapping("/loginLog/{ids}")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_LOGINLOG_REMOVE, PermissionConstants.SYSTEM_LOGINLOG_LIST}, logical = Logical.AND)
    @ApiOperation(value = "删除登录日志")
    public MoliResult<Boolean> deleteLoginLog(@PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        if (idList.isEmpty()) {
            return MoliResult.success(true);
        }
        sysLoginLogMapper.deleteBatchIds(idList);
        return MoliResult.success(true);
    }

    @DeleteMapping("/loginLog/clean")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_LOGINLOG_REMOVE, PermissionConstants.SYSTEM_LOGINLOG_LIST}, logical = Logical.AND)
    @ApiOperation(value = "清空登录日志")
    public MoliResult<Boolean> cleanLoginLog() {
        sysLoginLogMapper.delete(null);
        return MoliResult.success(true);
    }

    @GetMapping("/operationLogList")
    @RequiresPermissions(PermissionConstants.SYSTEM_OPERLOG_LIST)
    @ApiOperation(value = "操作日志列表")
    public MoliResult<PageRes<SysOperationLog>> operationLogList(SysOperationLog req) {

        PageRes<SysOperationLog> result = new PageRes<>();

        Page<SysOperationLog> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<SysOperationLog> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(req.getUserName())) {
            lambdaQueryWrapper.like(SysOperationLog::getUserName, req.getUserName());
        }
        if (StringUtils.isNotBlank(req.getTitle())) {
            lambdaQueryWrapper.like(SysOperationLog::getTitle, req.getTitle());
        }

        if (req.getStatus() != null) {
            lambdaQueryWrapper.eq(SysOperationLog::getStatus, req.getStatus());
        }
        if (req.getBusinessType() != null) {
            lambdaQueryWrapper.eq(SysOperationLog::getBusinessType, req.getBusinessType());
        }

        lambdaQueryWrapper.orderByDesc(SysOperationLog::getCreateTime);
        sysOperationLogMapper.selectPage(page, lambdaQueryWrapper);
        result.setList(page.getRecords());
        result.setPageNum(req.getPageNum());
        result.setPageSize(req.getPageSize());
        result.setTotal((int) page.getTotal());
        return MoliResult.success(result);
    }

    @DeleteMapping("/operationLog/{ids}")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_OPERLOG_REMOVE, PermissionConstants.SYSTEM_OPERLOG_LIST}, logical = Logical.AND)
    @ApiOperation(value = "删除操作日志")
    public MoliResult<Boolean> deleteOperationLog(@PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        if (idList.isEmpty()) {
            return MoliResult.success(true);
        }
        sysOperationLogMapper.deleteBatchIds(idList);
        return MoliResult.success(true);
    }

    @DeleteMapping("/operationLog/clean")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_OPERLOG_REMOVE, PermissionConstants.SYSTEM_OPERLOG_LIST}, logical = Logical.AND)
    @ApiOperation(value = "清空操作日志")
    public MoliResult<Boolean> cleanOperationLog() {
        sysOperationLogMapper.delete(null);
        return MoliResult.success(true);
    }

}
