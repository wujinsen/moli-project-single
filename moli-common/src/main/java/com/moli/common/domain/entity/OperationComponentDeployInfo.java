package com.moli.common.domain.entity;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OperationComponentDeployInfo extends BaseEntity {

    @ApiModelProperty("组件名")
    private String componentName;
    @ApiModelProperty("服务器ip")
    private String serverIp;
    @ApiModelProperty("账户名")
    private String account;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("部署路径")
    private String deployPath;
    @ApiModelProperty("端口号")
    private String port;
    @ApiModelProperty("版本号")
    private String version;
    @ApiModelProperty("开发环境: 1: dev 2:test 3:pre 4:pro")
    private Integer environment;
    private String remark;

}
