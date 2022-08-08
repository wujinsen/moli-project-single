package com.moli.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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


    /**
     * 添加岗位
     *
     * @return 添加岗位
     */
    @PostMapping
    public MoliResult<Boolean> insert(@RequestBody SysPost post) {
        postMapper.insert(post);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 更新岗位
     *
     * @return
     */
    @PutMapping
    public MoliResult<Boolean> update(@RequestBody SysPost post) {
        postMapper.updateById(post);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 查询单个岗位
     */
    @GetMapping(value = "/{id}")
    public MoliResult<SysPost> selectOne(@PathVariable Long id) {

        return MoliResult.success(postMapper.selectById(id));
    }

    /**
     * 删除岗位
     */
    @DeleteMapping("/{ids}")
    public MoliResult remove(@PathVariable Long[] ids) {
        for (Long id : ids) {
            postMapper.deleteById(id);
        }
        return MoliResult.success(Boolean.TRUE);
    }

}
