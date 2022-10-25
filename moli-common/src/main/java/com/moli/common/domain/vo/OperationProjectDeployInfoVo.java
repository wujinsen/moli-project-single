package com.moli.common.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OperationProjectDeployInfoVo {
    @ApiModelProperty("服务器id")
    private Long serverId;
    @ApiModelProperty("服务器id")
    private String serverIp;
    @ApiModelProperty("项目名称")
    private String projectName;
    @ApiModelProperty("端口号")
    private String deployPaht;
    @ApiModelProperty("端口号")
    private String port;
    private String remark;
}
