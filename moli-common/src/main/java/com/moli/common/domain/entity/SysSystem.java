package com.moli.common.domain.entity;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysSystem extends BaseEntity {

    @ApiModelProperty("系统编码（唯一）")
    private String systemCode;

    @ApiModelProperty("系统名称")
    private String systemName;

    @ApiModelProperty("访问地址（前端或子系统根 URL）")
    private String baseUrl;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("1启用 0停用")
    private Integer status;

    @ApiModelProperty("INTERNAL 本集群菜单 / EXTERNAL Ticket 跳转")
    private String ssoMode;

    @ApiModelProperty("SSO 入口路径，默认 /sso/login")
    private String entryPath;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("门户分组：governance/business/ai/tech/ops/data/office")
    private String systemGroup;

}
