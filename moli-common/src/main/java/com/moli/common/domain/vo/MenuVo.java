package com.moli.common.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.moli.common.core.BaseEntity;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class MenuVo  extends BaseEntity {

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "路由名称")
    private String name;

    @ApiModelProperty(value = "父级菜单ID")
    private Long parentId;

    @ApiModelProperty(value = "菜单路由")
    private String path;

    @ApiModelProperty(value = "菜单路由名称")
    private String component;

    @ApiModelProperty(value = "菜单类型 M目录 C菜单 F按钮")
    @NotNull(message = "菜单类型不能为空")
    private String menuType;

    @ApiModelProperty(value = "是否开启 1:启用 0:禁用")
    private Integer status;

    @ApiModelProperty(value = "权限标识")
    private String perms;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;

    @ApiModelProperty(value = "下级菜单集合")
    private List<MenuVo> children;

    @ApiModelProperty(value = "是否缓存")
    private Boolean noCache;

    private String redirect;

    @ApiModelProperty(value = "是否隐藏")
    private Boolean hidden;

    @ApiModelProperty(value = "菜单元信息")
    private MenuMetaVo meta;

    private Boolean alwaysShow;

    private Long userId;

    @ApiModelProperty(value = "菜单元信息")
    private List<Long> menuIds;

}
