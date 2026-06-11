package com.moli.system.controller;



import com.moli.common.constant.PermissionConstants;

import com.moli.common.core.MoliResult;

import com.moli.common.domain.entity.SysAction;

import com.moli.common.domain.vo.ActionQueryVo;

import com.moli.common.domain.vo.ActionVo;

import com.moli.common.page.PageRes;

import com.moli.system.service.ActionService;

import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;

import org.apache.shiro.authz.annotation.Logical;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;



import java.util.Arrays;

import java.util.List;

import java.util.stream.Collectors;



@RestController

@RequestMapping("/action")

@Api(tags = "动作目录")

public class ActionController {



    @Autowired

    private ActionService actionService;



    @GetMapping("/list")

    @RequiresPermissions(PermissionConstants.SYSTEM_ROLE_LIST)

    @ApiOperation(value = "页面可分配动作", notes = "供角色授权 UI 使用")

    public MoliResult<List<ActionVo>> list(@RequestParam Long menuId) {

        return MoliResult.success(actionService.listByMenuId(menuId));

    }



    @GetMapping("/page")

    @RequiresPermissions(PermissionConstants.SYSTEM_MENU_LIST)

    @ApiOperation(value = "动作目录分页", notes = "动作管理后台")

    public MoliResult<PageRes<ActionVo>> page(ActionQueryVo query) {

        return MoliResult.success(actionService.page(query));

    }



    @GetMapping("/{id}")

    @RequiresPermissions(PermissionConstants.SYSTEM_MENU_LIST)

    @ApiOperation(value = "动作详情")

    public MoliResult<ActionVo> getInfo(@PathVariable Long id) {

        return MoliResult.success(actionService.getById(id));

    }



    @PostMapping

    @RequiresPermissions(value = {PermissionConstants.SYSTEM_MENU_EDIT, PermissionConstants.SYSTEM_MENU_LIST}, logical = Logical.AND)

    @ApiOperation(value = "新增动作")

    public MoliResult<Boolean> insert(@RequestBody SysAction action) {

        return MoliResult.success(actionService.save(action));

    }



    @PutMapping

    @RequiresPermissions(value = {PermissionConstants.SYSTEM_MENU_EDIT, PermissionConstants.SYSTEM_MENU_LIST}, logical = Logical.AND)

    @ApiOperation(value = "更新动作", notes = "不可修改 permCode")

    public MoliResult<Boolean> update(@RequestBody SysAction action) {

        return MoliResult.success(actionService.update(action));

    }



    @PutMapping("/changeStatus")

    @RequiresPermissions(value = {PermissionConstants.SYSTEM_MENU_EDIT, PermissionConstants.SYSTEM_MENU_LIST}, logical = Logical.AND)

    @ApiOperation(value = "动作状态变更")

    public MoliResult<Boolean> changeStatus(@RequestBody SysAction action) {

        return MoliResult.success(actionService.changeStatus(action.getId(), action.getStatus()));

    }



    @DeleteMapping("/{ids}")

    @RequiresPermissions(value = {PermissionConstants.SYSTEM_MENU_EDIT, PermissionConstants.SYSTEM_MENU_LIST}, logical = Logical.AND)

    @ApiOperation(value = "删除动作")

    public MoliResult<Boolean> remove(@PathVariable String ids) {

        List<Long> idList = Arrays.stream(ids.split(","))

                .map(String::trim)

                .filter(s -> !s.isEmpty())

                .map(Long::valueOf)

                .collect(Collectors.toList());

        return MoliResult.success(actionService.removeByIds(idList));

    }

}


