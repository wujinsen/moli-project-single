package com.moli.common.domain.entity;


import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DictData extends BaseEntity {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "所属字典类型id")
    private Long dictTypeId;

    @ApiModelProperty(value = "字典排序")
    private int sort;

    @ApiModelProperty(value = "字典key")
    private String dictKey;

    @ApiModelProperty(value = "字典value")
    private String dictValue;

    @ApiModelProperty(value = "字典类型")
    private String dictType;

    @ApiModelProperty(value = "状态（1正常 0停用）")
    private String status;


}
