package com.moli.common.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CapabilitiesVo {

    @ApiModelProperty("有效权限码列表")
    private List<String> permissions;

    @ApiModelProperty("是否拥有全部权限（超管）")
    private Boolean fullPermission;
}
