package com.moli.common.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SystemEnterVo {

    @ApiModelProperty("当前系统")
    private SystemVo currentSystem;

    @ApiModelProperty("INTERNAL 时返回菜单；EXTERNAL 时为空")
    private List<MenuVo> menuVoList;

    @ApiModelProperty("EXTERNAL 时 SSO 跳转完整 URL（含 ticket）")
    private String redirectUrl;

    @ApiModelProperty("认证中心 token，子系统可复用")
    private String hubToken;

    @ApiModelProperty("有效权限码")
    private java.util.List<String> permissions;

    @ApiModelProperty("是否拥有全部权限（超管）")
    private Boolean fullPermission;

}
