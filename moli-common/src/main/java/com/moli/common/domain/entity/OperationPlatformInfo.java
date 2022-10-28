package com.moli.common.domain.entity;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OperationPlatformInfo extends BaseEntity {

    @ApiModelProperty("平台名称")
    private String platformName;
    @ApiModelProperty("url")
    private String url;
    @ApiModelProperty("账户名")
    private String account;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("开发环境: 1: dev 2:test 3:pre 4:pro")
    private Integer environment;
    private String remark;

}
