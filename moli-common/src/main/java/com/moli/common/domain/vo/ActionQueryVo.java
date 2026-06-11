package com.moli.common.domain.vo;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ActionQueryVo extends BaseEntity {

    @ApiModelProperty("权限码（模糊）")
    private String permCode;

    @ApiModelProperty("显示名称（模糊）")
    private String name;

    @ApiModelProperty("关联 C 菜单")
    private Long menuId;

    @ApiModelProperty("状态 1启用 0停用")
    private Integer status;
}
