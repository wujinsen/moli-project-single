package com.moli.common.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class OperationServerComponent {

    private Long Id;
    @ApiModelProperty("服务器ID")
    private Long serverId;
    @ApiModelProperty("组件ID")
    private Long componentId;

}
