package com.moli.common.domain.vo;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class OperationServerInfoVo extends BaseEntity {

    @ApiModelProperty("服务器名")
    private String serverName;

    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("端口号")
    private String port;

    @ApiModelProperty("开发环境: 1: dev 2:test 3:pre 4:pro")
    private Integer environment;

    private String remark;
}
