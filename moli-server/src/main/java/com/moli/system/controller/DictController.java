package com.moli.system.controller;


import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.Menu;
import com.moli.common.domain.vo.MenuVo;
import com.moli.system.mapper.DictDataMapper;
import com.moli.system.mapper.DictTypeMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("dict")
@Api(tags = "字典管理")
@Slf4j
public class DictController {

    @Autowired
    private DictDataMapper dictDataMapper;

    @Autowired
    private DictTypeMapper dictTypeMapper;

//    /**
//     * 获取菜单列表
//     *
//     * @return 菜单列表
//     */
//    @GetMapping("list")
//    public MoliResult list() {
//        //   Long userId = ShiroUtils.getUserInfo().getId();
//        Long userId = 1l;
//        List<MenuVo> menuVoList = menuService.selectMenuListByUserId(userId);
//        return MoliResult.success(menuVoList);
//    }
//
//    /**
//     * 获取菜单列表
//     *
//     * @return 菜单列表
//     */
//    @PostMapping
//    public MoliResult<Boolean> insert(@RequestBody Menu menu) {
//        menuService.insert(menu);
//        return MoliResult.success(Boolean.TRUE);
//    }
//
//    /**
//     * 获取菜单列表
//     *
//     * @return 菜单列表
//     */
//    @PutMapping
//    public MoliResult<Boolean> update(@RequestBody Menu menu) {
//        menuService.update(menu);
//        return MoliResult.success(Boolean.TRUE);
//    }
//
//    /**
//     * 查询菜单
//     */
//    @GetMapping(value = "/{id}")
//    public MoliResult<Menu> getInfo(@PathVariable Long id) {
//
//        return MoliResult.success(menuMapper.selectById(id));
//    }
//
//    /**
//     * 删除菜单
//     */
//    @DeleteMapping("/{id}")
//    public MoliResult remove(@PathVariable("id") Long id) {
//        menuMapper.deleteById(id);
//        return MoliResult.success(Boolean.TRUE);
//
//    }

}
