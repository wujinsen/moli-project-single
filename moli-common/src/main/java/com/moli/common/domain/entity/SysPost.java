package com.moli.common.domain.entity;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysPost extends BaseEntity {

    @ApiModelProperty(value = "岗位编码")
    private String postCode;

    @ApiModelProperty(value = "岗位名称")
    private String postName;

    @ApiModelProperty(value = "岗位状态(1:正常, 0:停用)")
    private Integer status;

    @ApiModelProperty(value = "岗位排序")
    private Integer sort;


}
