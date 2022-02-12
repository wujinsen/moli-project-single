package com.moli.system.service;

import com.moli.common.domain.entity.Menu;
import com.moli.common.domain.vo.MenuVo;

import java.util.List;

public interface MenuService {

    /**
     * 添加菜单
     * @param menu
     * @return
     */
    public Boolean insert(Menu menu) ;

    /**
     * 更新菜单
     * @param menu
     * @return
     */
    public Boolean update(Menu menu) ;

    /**
     * 根据用户查询菜单树
     */
    List<MenuVo> selectMenuTreeByUserId(Long userId);

    /**
     * 根据用户查询菜单列表
     */
    List<MenuVo> selectMenuListByUserId(MenuVo menuVo);

    List<MenuVo> selectMenuTreeByRoleId(String roleId);

    List<MenuVo> getMenuTreeAll();

}
