package com.moli.system.controller;

import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.Post;
import com.moli.system.mapper.PostMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
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
     * 添加岗位
     *
     * @return 添加岗位
     */
    @PostMapping
    public MoliResult<Boolean> insert(@RequestBody Post post) {
        postMapper.insert(post);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 更新岗位
     *
     * @return
     */
    @PutMapping
    public MoliResult<Boolean> update(@RequestBody Post post) {
        postMapper.updateById(post);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 查询单个岗位
     */
    @GetMapping(value = "/{id}")
    public MoliResult<Post> selectOne(@PathVariable Long id) {

        return MoliResult.success(postMapper.selectById(id));
    }

    /**
     * 删除岗位
     */
    @DeleteMapping("/{id}")
    public MoliResult remove(@PathVariable("id") Long id) {

        postMapper.deleteById(id);
        return MoliResult.success(Boolean.TRUE);
    }

}
