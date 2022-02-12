package com.moli.system.controller;

import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.Menu;
import com.moli.common.domain.vo.MenuVo;
import com.moli.system.mapper.MenuMapper;
import com.moli.system.mapper.UserMapper;
import com.moli.system.service.MenuService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/menu")
@Api(tags = "菜单管理")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取菜单列表
     *
     * @return 菜单列表
     */
    @GetMapping("getRouters")
    public MoliResult<List<MenuVo>> getRouters() {
      //  Long userId2 = ShiroUtils.getUserInfo().getId();
        Long userId = 1l;
        List<MenuVo> menuVoList = menuService.selectMenuTreeByUserId(userId);

        return MoliResult.success(menuVoList);
    }

    /**
     * 获取菜单列表
     *
     * @return 菜单列表
     */
    @GetMapping("list")
    public MoliResult list(String menuName, Integer status) {
        //   Long userId = ShiroUtils.getUserInfo().getId();
        MenuVo menuVo = new MenuVo();
        menuVo.setUserId(1l);
        menuVo.setName(menuName);
        menuVo.setStatus(status);
        List<MenuVo> menuVoList = menuService.selectMenuListByUserId(menuVo);
        return MoliResult.success(menuVoList);
    }

    /**
     * 添加菜单
     *
     * @return
     */
    @PostMapping
    public MoliResult<Boolean> insert(@RequestBody Menu menu) {
        menuService.insert(menu);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 更新菜单
     *
     * @return
     */
    @PutMapping
    public MoliResult<Boolean> update(@RequestBody Menu menu) {
        menuService.update(menu);
        return MoliResult.success(Boolean.TRUE);
    }

    /**
     * 查询菜单
     */
    @GetMapping(value = "/{id}")
    public MoliResult<Menu> getInfo(@PathVariable Long id) {

        return MoliResult.success(menuMapper.selectById(id));
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public MoliResult remove(@PathVariable("id") Long id) {
        menuMapper.deleteById(id);
        return MoliResult.success(Boolean.TRUE);

    }

    /**
     * 获取菜单列表
     *
     * @return 菜单列表
     */
    @GetMapping("selectMenuTreeByUserId")
    public MoliResult selectMenuTreeByUserId() {
        MenuVo menuVo = new MenuVo();
        menuVo.setUserId(1l);
        List<MenuVo> menuVoList = menuService.selectMenuListByUserId(menuVo);
        return MoliResult.success(menuVoList);
    }

    /**
     *
     * @return 菜单列表
     */
    @GetMapping("selectMenuTreeByRoleId")
    public MoliResult selectMenuTreeByRoleId(String roleId) {

        List<MenuVo> menuVoList = menuService.selectMenuTreeByRoleId(roleId);
        return MoliResult.success(menuVoList);
    }
    /**
     * 获取菜单列表
     *
     * @return 菜单列表
     */
    @GetMapping("getMenuTreeAll")
    public MoliResult getMenuTreeAll() {

        List<MenuVo> menuVoList = menuService.getMenuTreeAll();
        return MoliResult.success(menuVoList);
    }
}

