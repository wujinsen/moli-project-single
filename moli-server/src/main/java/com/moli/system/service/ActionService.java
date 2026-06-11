package com.moli.system.service;



import com.moli.common.domain.entity.SysAction;

import com.moli.common.domain.vo.ActionQueryVo;

import com.moli.common.domain.vo.ActionVo;

import com.moli.common.page.PageRes;



import java.util.List;



public interface ActionService {



    List<ActionVo> listByMenuId(Long menuId);



    PageRes<ActionVo> page(ActionQueryVo query);



    ActionVo getById(Long id);



    boolean save(SysAction action);



    boolean update(SysAction action);



    boolean changeStatus(Long id, Integer status);



    boolean removeByIds(List<Long> ids);

}


