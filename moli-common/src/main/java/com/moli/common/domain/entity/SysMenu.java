package com.moli.common.domain.entity;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysMenu extends BaseEntity {

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "菜单名称(英文)")
    private String menuNameEn;

    @ApiModelProperty(value = "菜单名称(日文)")
    private String menuNameJa;

    @ApiModelProperty(value = "父ID")
    private Long parentId;

    @ApiModelProperty(value = "菜单路由")
    private String path;

    @ApiModelProperty(value = "菜单路由名称")
    private String component;

    @ApiModelProperty(value = "菜单类型 M目录 C菜单 F按钮")
    private String menuType;

    @ApiModelProperty(value = "权限标识")
    private String perms;

    @ApiModelProperty(value = "是否开启 1:启用 0:禁用")
    private Integer status;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;
}
