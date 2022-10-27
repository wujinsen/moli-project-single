package com.moli.operation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.OperationPlatformInfo;
import com.moli.common.page.PageRes;
import com.moli.operation.mapper.OperationPlatformMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/operation/platform")
@Api(tags = "运维平台管理")
@Slf4j
public class OperationPlatformController {

    @Resource
    private OperationPlatformMapper operationPlatformMapper;

    @GetMapping("/list")
    @ApiOperation(value = "运维平台列表", notes = "运维平台列表")
    public MoliResult<PageRes<OperationPlatformInfo>> list(OperationPlatformInfo operationPlatformInfo) {
        PageRes<OperationPlatformInfo> result = new PageRes<>();
        LambdaQueryWrapper<OperationPlatformInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(operationPlatformInfo.getPlatformName())) {
            lambdaQueryWrapper.like(OperationPlatformInfo::getPlatformName, operationPlatformInfo.getPlatformName());
        }

        if (operationPlatformInfo.getEnvironment() != null) {
            lambdaQueryWrapper.eq(OperationPlatformInfo::getEnvironment, operationPlatformInfo.getEnvironment());
        }

        lambdaQueryWrapper.orderByDesc(OperationPlatformInfo::getCreateTime);
        Page page = new Page();
        page.setCurrent(operationPlatformInfo.getPageNum());
        page.setSize(operationPlatformInfo.getPageSize());
        operationPlatformMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(page.getRecords());
        result.setPageNum(operationPlatformInfo.getPageNum());
        result.setPageSize(operationPlatformInfo.getPageSize());
        return MoliResult.success(result);

    }

    @PostMapping
    @ApiOperation(value = "添加运维平台", notes = "添加运维平台")
    public MoliResult<Boolean> insert(@RequestBody OperationPlatformInfo operationPlatformInfo) {
        operationPlatformMapper.insert(operationPlatformInfo);
        return MoliResult.success(Boolean.TRUE);
    }


    @PutMapping
    @ApiOperation(value = "更新运维平台", notes = "更新运维平台")
    public MoliResult<Boolean> update(@RequestBody OperationPlatformInfo operationPlatformInfo) {
        operationPlatformMapper.updateById(operationPlatformInfo);
        return MoliResult.success(Boolean.TRUE);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询单个运维平台", notes = "查询单个运维平台")
    public MoliResult<OperationPlatformInfo> selectOne(@PathVariable Long id) {

        return MoliResult.success(operationPlatformMapper.selectById(id));
    }

    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除运维平台", notes = "删除运维平台")
    public MoliResult remove(@PathVariable Long[] ids) {
        for (Long id : ids) {
            operationPlatformMapper.deleteById(id);
        }
        return MoliResult.success(Boolean.TRUE);
    }
}
