package com.moli.common.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemVo {

    @ApiModelProperty("系统ID")
    private Long id;

    @ApiModelProperty("系统编码")
    private String systemCode;

    @ApiModelProperty("系统名称")
    private String systemName;

    @ApiModelProperty("访问地址")
    private String baseUrl;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("INTERNAL / EXTERNAL")
    private String ssoMode;

    @ApiModelProperty("是否默认系统")
    private Boolean isDefault;

    @ApiModelProperty("门户分组：governance/business/ai/tech/ops/data/office")
    private String systemGroup;

}
