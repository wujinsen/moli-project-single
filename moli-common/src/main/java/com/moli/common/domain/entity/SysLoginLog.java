package com.moli.common.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SysLoginLog {
    private String id;
    @ApiModelProperty("用户名")
    private String realName;
    @ApiModelProperty("手机号")
    private String telephone;
    @ApiModelProperty("IP地址")
    private String ipAddress;
    @ApiModelProperty("登录地址")
    private String loginAddress;
    @ApiModelProperty("浏览器")
    private String browser;
    @ApiModelProperty("操作系统")
    private String os;
    @ApiModelProperty("登录状态（1成功 0失败）")
    private Integer status;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("登录时间")
    private Date loginTime;
}
