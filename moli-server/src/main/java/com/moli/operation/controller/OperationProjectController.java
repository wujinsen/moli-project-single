package com.moli.operation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.OperationProjectDeployInfo;
import com.moli.common.domain.entity.OperationServerInfo;
import com.moli.common.domain.vo.OperationServerInfoVo;
import com.moli.common.page.PageRes;
import com.moli.operation.mapper.OperationProjectDeployInfoMapper;
import com.moli.operation.mapper.OperationServerMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/operation/project")
@Api(tags = "项目管理")
@Slf4j
public class OperationProjectController {

    @Resource
    private OperationProjectDeployInfoMapper operationProjectDeployInfoMapper;

    @GetMapping("/list")
    @ApiOperation(value = "项目列表", notes = "项目列表")
    public MoliResult<PageRes<OperationProjectDeployInfo>> list(OperationProjectDeployInfo operationProjectDeployInfo) {
        PageRes<OperationProjectDeployInfo> result = new PageRes<>();
        LambdaQueryWrapper<OperationProjectDeployInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(operationProjectDeployInfo.getProjectName())) {
            lambdaQueryWrapper.eq(OperationProjectDeployInfo::getProjectName, operationProjectDeployInfo.getProjectName());
        }
        if (StringUtils.isNotBlank(operationProjectDeployInfo.getServerIp())) {
            lambdaQueryWrapper.eq(OperationProjectDeployInfo::getServerIp, operationProjectDeployInfo.getServerIp());
        }

        lambdaQueryWrapper.orderByDesc(OperationProjectDeployInfo::getCreateTime);
        Page page = new Page();
        page.setCurrent(operationProjectDeployInfo.getPageNum());
        page.setSize(operationProjectDeployInfo.getPageSize());
        operationProjectDeployInfoMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(page.getRecords());
        result.setPageNum(operationProjectDeployInfo.getPageNum());
        result.setPageSize(operationProjectDeployInfo.getPageSize());
        return MoliResult.success(result);

    }

    @PostMapping
    @ApiOperation(value = "添加项目", notes = "添加项目")
    public MoliResult<Boolean> insert(@RequestBody OperationProjectDeployInfo operationProjectDeployInfo) {
        operationProjectDeployInfoMapper.insert(operationProjectDeployInfo);
        return MoliResult.success(Boolean.TRUE);
    }


    @PutMapping
    @ApiOperation(value = "更新项目", notes = "更新项目")
    public MoliResult<Boolean> update(@RequestBody OperationProjectDeployInfo operationProjectDeployInfo) {
        operationProjectDeployInfoMapper.updateById(operationProjectDeployInfo);
        return MoliResult.success(Boolean.TRUE);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询单个项目", notes = "查询单个项目")
    public MoliResult<OperationProjectDeployInfo> selectOne(@PathVariable Long id) {

        return MoliResult.success(operationProjectDeployInfoMapper.selectById(id));
    }

    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除项目", notes = "删除项目")
    public MoliResult remove(@PathVariable Long[] ids) {
        for (Long id : ids) {
            operationProjectDeployInfoMapper.deleteById(id);
        }
        return MoliResult.success(Boolean.TRUE);
    }
}
