package com.moli.common.domain.entity;


import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DictData extends BaseEntity {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "字典排序")
    private Integer sort;

    @ApiModelProperty(value = "字典key")
    private String dictKey;

    @ApiModelProperty(value = "字典value")
    private String dictValue;

    @ApiModelProperty(value = "字典类型")
    private String dictType;

    @ApiModelProperty(value = "状态（1正常 0停用）")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;
}
