package com.moli.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.PermissionConstants;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysPost;
import com.moli.common.domain.vo.PostVo;
import com.moli.common.page.PageRes;
import com.moli.system.mapper.PostMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;


@RestController
@RequestMapping("post")
@Api(tags = "岗位管理")
@Slf4j
public class PostController {

    @Autowired
    private PostMapper postMapper;

    /**
     * 岗位列表
     *
     * @param postVo
     * @return
     */
    @GetMapping("/list")
    @RequiresPermissions(PermissionConstants.SYSTEM_POST_LIST)
    @ApiOperation(value = "岗位列表", notes = "岗位列表")
    public MoliResult<PageRes<SysPost>> list(PostVo postVo) {
        PageRes<SysPost> result = new PageRes<>();
        LambdaQueryWrapper<SysPost> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(postVo.getPostCode())) {
            lambdaQueryWrapper.eq(SysPost::getPostCode, postVo.getPostCode());
        }
        if (StringUtils.isNotBlank(postVo.getPostName())) {
            lambdaQueryWrapper.like(SysPost::getPostName, postVo.getPostName());
        }
        if (postVo.getStatus() != null) {
            lambdaQueryWrapper.eq(SysPost::getStatus, postVo.getStatus());
        }
        lambdaQueryWrapper.orderByDesc(SysPost::getCreateTime);
        Page page = new Page();
        page.setCurrent(postVo.getPageNum());
        page.setSize(postVo.getPageSize());
        postMapper.selectPage(page, lambdaQueryWrapper);
        Long total = page.getTotal();
        result.setTotal(total.intValue());
        result.setList(page.getRecords());
        result.setPageNum(postVo.getPageNum());
        result.setPageSize(postVo.getPageSize());
        return MoliResult.success(result);

    }

    @PostMapping
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_POST_ADD, PermissionConstants.SYSTEM_POST_LIST}, logical = Logical.AND)
    @ApiOperation(value = "添加岗位", notes = "添加岗位")
    public MoliResult<Boolean> insert(@RequestBody SysPost post) {
        postMapper.insert(post);
        return MoliResult.success(Boolean.TRUE);
    }

    @PutMapping
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_POST_EDIT, PermissionConstants.SYSTEM_POST_LIST}, logical = Logical.AND)
    @ApiOperation(value = "更新岗位", notes = "更新岗位")
    public MoliResult<Boolean> update(@RequestBody SysPost post) {
        postMapper.updateById(post);
        return MoliResult.success(Boolean.TRUE);
    }

    @GetMapping(value = "/{id}")
    @RequiresPermissions(PermissionConstants.SYSTEM_POST_LIST)
    @ApiOperation(value = "查询单个岗位", notes = "查询单个岗位")
    public MoliResult<SysPost> selectOne(@PathVariable Long id) {

        return MoliResult.success(postMapper.selectById(id));
    }


    @DeleteMapping("/{ids}")
    @RequiresPermissions(value = {PermissionConstants.SYSTEM_POST_REMOVE, PermissionConstants.SYSTEM_POST_LIST}, logical = Logical.AND)
    @ApiOperation(value = "删除岗位", notes = "删除岗位")
    public MoliResult remove(@PathVariable Long[] ids) {
        for (Long id : ids) {
            postMapper.deleteById(id);
        }
        return MoliResult.success(Boolean.TRUE);
    }


    @GetMapping(value = "/allPost")
    @RequiresPermissions(PermissionConstants.SYSTEM_POST_LIST)
    @ApiOperation(value = "查询所有岗位", notes = "查询所有岗位")
    public MoliResult<List<SysPost>> allPost() {
        return MoliResult.success(postMapper.selectList(new LambdaQueryWrapper<>()));
    }

}
