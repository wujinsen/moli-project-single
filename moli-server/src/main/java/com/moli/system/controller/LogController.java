package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysLoginLog;
import com.moli.common.domain.entity.SysOperationLog;
import com.moli.common.page.PageReq;
import com.moli.common.page.PageRes;
import com.moli.system.mapper.SysLoginLogMapper;
import com.moli.system.mapper.SysOperationLogMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(tags = "系统操作日志管理")
@RequestMapping("/log")
public class LogController {

    @Autowired
    private SysLoginLogMapper sysLoginLogMapper;

    @Autowired
    private SysOperationLogMapper sysOperationLogMapper;

    @GetMapping("/loginLogList")
    @ApiOperation(value = "登录日志列表")
    public MoliResult<PageRes<SysLoginLog>> loginLogList(@RequestBody SysLoginLog sysLoginLog) {
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

    @PostMapping("/oepartionLogList")
    @ApiOperation(value = "操作日志列表")
    public MoliResult<PageRes<SysOperationLog>> oepartionLogList(@RequestBody PageReq<SysOperationLog> req) {

        PageRes<SysOperationLog> result = new PageRes<>();

        Page<SysOperationLog> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<SysOperationLog> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (req.getData() != null) {
            if (StringUtils.isNotBlank(req.getData().getUserName())) {
                lambdaQueryWrapper.eq(SysOperationLog::getUserName, req.getData().getUserName());
            }
            if (StringUtils.isNotBlank(req.getData().getTelephone())) {
                lambdaQueryWrapper.eq(SysOperationLog::getTelephone, req.getData().getTelephone());
            }
            if (req.getData().getStatus() != null) {
                lambdaQueryWrapper.eq(SysOperationLog::getStatus, req.getData().getStatus());
            }
            if (req.getData().getBusinessType() != null) {
                lambdaQueryWrapper.eq(SysOperationLog::getBusinessType, req.getData().getBusinessType());
            }
        }
        lambdaQueryWrapper.orderByDesc(SysOperationLog::getCreateTime);
        sysOperationLogMapper.selectPage(page, lambdaQueryWrapper);
        result.setList(page.getRecords());
        result.setPageNum(req.getPageNum());
        result.setPageSize(req.getPageSize());
        result.setTotal((int) page.getTotal());
        return MoliResult.success(result);
    }

}
