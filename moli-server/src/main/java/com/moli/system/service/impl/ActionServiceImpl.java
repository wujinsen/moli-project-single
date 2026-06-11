package com.moli.system.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.moli.common.constant.CommonConstant;

import com.moli.common.domain.entity.SysAction;

import com.moli.common.domain.entity.SysMenu;

import com.moli.common.domain.entity.SysRoleAction;

import com.moli.common.domain.vo.ActionQueryVo;

import com.moli.common.domain.vo.ActionVo;

import com.moli.common.exception.BaseException;

import com.moli.common.page.PageRes;

import com.moli.system.mapper.ActionMapper;

import com.moli.system.mapper.MenuMapper;

import com.moli.system.mapper.RoleActionMapper;

import com.moli.system.service.ActionService;

import org.apache.commons.collections.CollectionUtils;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;



import javax.annotation.Resource;

import java.util.Collections;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import java.util.stream.Collectors;



@Service

public class ActionServiceImpl implements ActionService {



    @Resource

    private ActionMapper actionMapper;



    @Resource

    private MenuMapper menuMapper;



    @Resource

    private RoleActionMapper roleActionMapper;



    @Override

    public List<ActionVo> listByMenuId(Long menuId) {

        if (menuId == null) {

            return Collections.emptyList();

        }

        List<SysAction> actions = actionMapper.selectList(new LambdaQueryWrapper<SysAction>()

                .eq(SysAction::getMenuId, menuId)

                .eq(SysAction::getStatus, CommonConstant.YES)

                .orderByAsc(SysAction::getOrderNum)

                .orderByAsc(SysAction::getId));

        return actions.stream().map(this::toVo).collect(Collectors.toList());

    }



    @Override

    public PageRes<ActionVo> page(ActionQueryVo query) {

        PageRes<ActionVo> result = new PageRes<>();

        LambdaQueryWrapper<SysAction> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(query.getPermCode())) {

            wrapper.like(SysAction::getPermCode, query.getPermCode().trim());

        }

        if (StringUtils.isNotBlank(query.getName())) {

            wrapper.like(SysAction::getName, query.getName().trim());

        }

        if (query.getMenuId() != null) {

            wrapper.eq(SysAction::getMenuId, query.getMenuId());

        }

        if (query.getStatus() != null) {

            wrapper.eq(SysAction::getStatus, query.getStatus());

        }

        wrapper.orderByAsc(SysAction::getOrderNum).orderByAsc(SysAction::getId);



        Page<SysAction> page = new Page<>(query.getPageNum(), query.getPageSize());

        actionMapper.selectPage(page, wrapper);

        Map<Long, String> menuNames = loadMenuNames(page.getRecords());

        result.setList(page.getRecords().stream()

                .map(action -> toVo(action, menuNames.get(action.getMenuId())))

                .collect(Collectors.toList()));

        result.setTotal((int) page.getTotal());

        result.setPageNum(query.getPageNum());

        result.setPageSize(query.getPageSize());

        return result;

    }



    @Override

    public ActionVo getById(Long id) {

        SysAction action = actionMapper.selectById(id);

        if (action == null) {

            return null;

        }

        String menuName = null;

        if (action.getMenuId() != null) {

            SysMenu menu = menuMapper.selectById(action.getMenuId());

            if (menu != null) {

                menuName = menu.getMenuName();

            }

        }

        return toVo(action, menuName);

    }



    @Override

    @Transactional(rollbackFor = Exception.class)

    public boolean save(SysAction action) {

        validateForSave(action, true);

        fillResourceAndAction(action);

        if (action.getStatus() == null) {

            action.setStatus(CommonConstant.YES);

        }

        if (action.getOrderNum() == null) {

            action.setOrderNum(0);

        }

        return actionMapper.insert(action) > 0;

    }



    @Override

    @Transactional(rollbackFor = Exception.class)

    public boolean update(SysAction action) {

        if (action.getId() == null) {

            throw new BaseException("动作ID不能为空");

        }

        SysAction existing = actionMapper.selectById(action.getId());

        if (existing == null) {

            throw new BaseException("动作不存在");

        }

        action.setPermCode(existing.getPermCode());

        action.setResource(existing.getResource());

        action.setAction(existing.getAction());

        validateForSave(action, false);

        return actionMapper.updateById(action) > 0;

    }



    @Override

    @Transactional(rollbackFor = Exception.class)

    public boolean changeStatus(Long id, Integer status) {

        if (id == null || status == null) {

            throw new BaseException("参数不完整");

        }

        SysAction action = new SysAction();

        action.setId(id);

        action.setStatus(status);

        return actionMapper.updateById(action) > 0;

    }



    @Override

    @Transactional(rollbackFor = Exception.class)

    public boolean removeByIds(List<Long> ids) {

        if (CollectionUtils.isEmpty(ids)) {

            return true;

        }

        List<SysAction> actions = actionMapper.selectList(new LambdaQueryWrapper<SysAction>().in(SysAction::getId, ids));

        if (CollectionUtils.isEmpty(actions)) {

            return true;

        }

        List<String> permCodes = actions.stream().map(SysAction::getPermCode).collect(Collectors.toList());

        roleActionMapper.delete(new LambdaQueryWrapper<SysRoleAction>().in(SysRoleAction::getPermCode, permCodes));

        actionMapper.deleteBatchIds(ids);

        return true;

    }



    private void validateForSave(SysAction action, boolean isCreate) {

        if (action == null) {

            throw new BaseException("动作不能为空");

        }

        if (isCreate) {

            if (StringUtils.isBlank(action.getPermCode())) {

                throw new BaseException("权限码不能为空");

            }

            String permCode = action.getPermCode().trim();

            action.setPermCode(permCode);

            validatePermCodeFormat(permCode);

            Integer count = actionMapper.selectCount(new LambdaQueryWrapper<SysAction>().eq(SysAction::getPermCode, permCode));

            if (count != null && count > 0) {

                throw new BaseException("权限码已存在: " + permCode);

            }

        }

        if (StringUtils.isBlank(action.getName())) {

            throw new BaseException("显示名称不能为空");

        }

        action.setName(action.getName().trim());

        if (action.getMenuId() != null) {

            SysMenu menu = menuMapper.selectById(action.getMenuId());

            if (menu == null || !"C".equalsIgnoreCase(menu.getMenuType())) {

                throw new BaseException("关联菜单须为页面类型(C)");

            }

        }

    }



    private void validatePermCodeFormat(String permCode) {

        String[] parts = permCode.split(":");

        if (parts.length != 3 || StringUtils.isAnyBlank(parts[0], parts[1], parts[2])) {

            throw new BaseException("权限码格式须为 模块:资源:操作，如 system:user:add");

        }

    }



    private void fillResourceAndAction(SysAction action) {

        String[] parts = action.getPermCode().split(":");

        action.setResource(parts[1]);

        action.setAction(parts[2]);

    }



    private Map<Long, String> loadMenuNames(List<SysAction> actions) {

        List<Long> menuIds = actions.stream()

                .map(SysAction::getMenuId)

                .filter(id -> id != null)

                .distinct()

                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(menuIds)) {

            return Collections.emptyMap();

        }

        List<SysMenu> menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getId, menuIds));

        Map<Long, String> map = new HashMap<>();

        for (SysMenu menu : menus) {

            map.put(menu.getId(), menu.getMenuName());

        }

        return map;

    }



    private ActionVo toVo(SysAction action) {

        return toVo(action, null);

    }



    private ActionVo toVo(SysAction action, String menuName) {

        ActionVo vo = new ActionVo();

        vo.setId(action.getId());

        vo.setPermCode(action.getPermCode());

        vo.setName(action.getName());

        vo.setMenuId(action.getMenuId());

        vo.setOrderNum(action.getOrderNum());

        vo.setResource(action.getResource());

        vo.setAction(action.getAction());

        vo.setStatus(action.getStatus());

        vo.setMenuName(menuName);

        return vo;

    }

}


