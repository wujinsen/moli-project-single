package com.moli.common.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OperationProjectDeployInfo {
    @ApiModelProperty("服务器id")
    private Long serverId;
    @ApiModelProperty("端口号")
    private String projectName;
    @ApiModelProperty("端口号")
    private String deployPaht;
    @ApiModelProperty("端口号")
    private Integer port;
}
