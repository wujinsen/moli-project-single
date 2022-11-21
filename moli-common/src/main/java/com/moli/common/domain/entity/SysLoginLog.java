package com.moli.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class SysLoginLog {

    private Long id;
    @ApiModelProperty("用户名")
    private String userName;
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

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;


}
