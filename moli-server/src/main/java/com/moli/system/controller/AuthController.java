package com.moli.system.controller;

import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.vo.CapabilitiesVo;
import com.moli.config.util.ShiroUtils;
import com.moli.system.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Api(tags = "认证与权限")
public class AuthController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/capabilities")
    @ApiOperation(value = "当前用户有效权限", notes = "F5 / 缓存缺失时补拉 permissions")
    public MoliResult<CapabilitiesVo> capabilities() {
        SysUser user = ShiroUtils.getUserInfo();
        return MoliResult.success(permissionService.buildCapabilities(user.getId(), user.getUserName()));
    }
}
