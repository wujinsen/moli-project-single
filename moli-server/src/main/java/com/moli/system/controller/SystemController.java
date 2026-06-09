package com.moli.system.controller;

import com.moli.common.constant.CommonConstant;
import com.moli.common.constant.PermissionConstants;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysSystem;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.vo.SystemEnterReq;
import com.moli.common.domain.vo.SystemEnterVo;
import com.moli.common.domain.vo.SystemVo;
import com.moli.common.page.PageRes;
import com.moli.config.util.ShiroUtils;
import com.moli.system.service.SysSystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system")
@Api(tags = "业务系统注册与 SSO 门户", description = "moli-admin；用户系统分配见 UserController#insertUserSystem")
public class SystemController {

    @Autowired
    private SysSystemService sysSystemService;

    @GetMapping("/my")
    @ApiOperation(value = "当前用户可访问的系统")
    public MoliResult<List<SystemVo>> mySystems() {
        SysUser user = ShiroUtils.getUserInfo();
        return MoliResult.success(sysSystemService.listByUserId(user.getId(), user.getUserName()));
    }

    @PostMapping("/enter")
    @ApiOperation(value = "进入系统", notes = "INTERNAL 返回菜单；EXTERNAL 返回 redirectUrl")
    public MoliResult<SystemEnterVo> enter(@RequestBody SystemEnterReq req) {
        SysUser user = ShiroUtils.getUserInfo();
        return MoliResult.success(sysSystemService.enterSystem(user.getId(), user.getUserName(), req.getSystemId()));
    }

    @PostMapping("/switch")
    @ApiOperation(value = "切换系统", notes = "与 enter 相同，复用同一 Shiro Session")
    public MoliResult<SystemEnterVo> switchSystem(@RequestBody SystemEnterReq req) {
        return enter(req);
    }

    @GetMapping("/list")
    @RequiresPermissions(PermissionConstants.SYSTEM_USER_LIST)
    @ApiOperation(value = "系统列表（管理）")
    public MoliResult<PageRes<SysSystem>> list(SysSystem query) {
        return MoliResult.success(sysSystemService.page(query));
    }

    @PostMapping
    @RequiresPermissions(PermissionConstants.SYSTEM_USER_LIST)
    @ApiOperation(value = "新增系统")
    public MoliResult<Boolean> insert(@RequestBody SysSystem system) {
        if (!CommonConstant.isSuperAdmin(ShiroUtils.getUserInfo().getUserName())) {
            return MoliResult.errorMsg(com.moli.common.enums.ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "仅特殊管理员可维护系统注册");
        }
        return MoliResult.success(sysSystemService.saveSystem(system));
    }

    @PutMapping
    @RequiresPermissions(PermissionConstants.SYSTEM_USER_LIST)
    @ApiOperation(value = "更新系统")
    public MoliResult<Boolean> update(@RequestBody SysSystem system) {
        if (!CommonConstant.isSuperAdmin(ShiroUtils.getUserInfo().getUserName())) {
            return MoliResult.errorMsg(com.moli.common.enums.ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "仅特殊管理员可维护系统注册");
        }
        return MoliResult.success(sysSystemService.updateSystem(system));
    }

    @DeleteMapping("/{ids}")
    @RequiresPermissions(PermissionConstants.SYSTEM_USER_LIST)
    @ApiOperation(value = "删除系统")
    public MoliResult<Boolean> delete(@PathVariable String ids) {
        if (!CommonConstant.isSuperAdmin(ShiroUtils.getUserInfo().getUserName())) {
            return MoliResult.errorMsg(com.moli.common.enums.ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "仅特殊管理员可维护系统注册");
        }
        List<Long> idList = Arrays.stream(ids.split(","))
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return MoliResult.success(sysSystemService.deleteSystems(idList));
    }

}
