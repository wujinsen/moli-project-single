package com.moli.common.domain.vo;

import com.moli.common.domain.entity.SysUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class LoginVo {

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "用户信息")
    private SysUser user;

    @ApiModelProperty(value = "菜单树（多系统时进入 INTERNAL 系统后才有）")
    private List<MenuVo> menuVoList;

    @ApiModelProperty(value = "可访问的业务系统列表")
    private List<SystemVo> systemList;

    @ApiModelProperty(value = "当前已进入的系统")
    private SystemVo currentSystem;

    @ApiModelProperty(value = "是否启用多系统门户")
    private Boolean systemPortalEnabled;

}
