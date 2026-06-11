package com.moli.common.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RoleAuthVo {

    @ApiModelProperty("已授权菜单 ID（M/C）")
    private List<Long> menuIds;

    @ApiModelProperty("已授权动作码")
    private List<String> actionCodes;
}
