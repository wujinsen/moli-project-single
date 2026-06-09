package com.moli.common.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserSystem {

    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("系统ID")
    private Long systemId;

    @ApiModelProperty("是否默认系统 1是 0否")
    private Integer isDefault;

}
