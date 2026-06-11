package com.moli.common.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActionVo {

    @ApiModelProperty("动作 ID")
    private Long id;

    @ApiModelProperty("权限码")
    private String permCode;

    @ApiModelProperty("显示名称")
    private String name;

    @ApiModelProperty("关联 C 菜单")
    private Long menuId;

    @ApiModelProperty("排序")
    private Integer orderNum;

    @ApiModelProperty("资源")
    private String resource;

    @ApiModelProperty("动作")
    private String action;

    @ApiModelProperty("状态 1启用 0停用")
    private Integer status;

    @ApiModelProperty("关联页面名称（展示）")
    private String menuName;
}
