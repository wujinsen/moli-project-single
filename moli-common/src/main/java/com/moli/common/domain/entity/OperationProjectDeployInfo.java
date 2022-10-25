package com.moli.common.domain.entity;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OperationProjectDeployInfo extends BaseEntity {
    @ApiModelProperty("服务器id")
    private Long serverId;
    @ApiModelProperty("服务器id")
    private String serverIp;
    @ApiModelProperty("项目名称")
    private String projectName;
    @ApiModelProperty("端口号")
    private String deployPath;
    @ApiModelProperty("端口号")
    private String port;
    @ApiModelProperty("开发环境: 1: dev 2:test 3:pre 4:pro")
    private Integer environment;
    private String remark;
}
