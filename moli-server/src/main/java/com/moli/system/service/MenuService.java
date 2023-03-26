package com.moli.system.service;

import com.moli.common.domain.entity.SysMenu;
import com.moli.common.domain.vo.MenuVo;

import java.util.List;

public interface MenuService {

    /**
     * 添加菜单
     * @param menu
     * @return
     */
    public Boolean insert(SysMenu menu) ;

    /**
     * 更新菜单
     * @param menu
     * @return
     */
    public Boolean update(SysMenu menu) ;

    /**
     * 根据用户查询菜单树
     */
    List<MenuVo> selectMenuTreeByUserId(Long userId);

    /**
     * 获取菜单列表
     * @param menuVo
     * @return
     */
    List<MenuVo> selectMenuList(MenuVo menuVo);
    /**
     * 根据用户查询菜单列表
     */
    List<MenuVo> selectMenuListByUserId(MenuVo menuVo);

    /**
     * 根据角色获取菜单树
     * @param roleId
     * @return
     */
    List<MenuVo> selectMenuTreeByRoleId(Long roleId);

    /**
     * 所有菜单列表树结构
     * @return
     */
    List<MenuVo> getMenuTreeAll();

}
