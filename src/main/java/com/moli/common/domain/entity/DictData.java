package com.moli.common.domain.entity;


import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DictData extends BaseEntity {

    @ApiModelProperty(value = "字典编码")
    private Long dictCode;

    @ApiModelProperty(value = "字典排序")
    private Long dictSort;

    @ApiModelProperty(value = "字典标签")
    private String dictLabel;

    @ApiModelProperty(value = "字典键值")
    private String dictValue;

    @ApiModelProperty(value = "字典类型")
    private String dictType;

    @ApiModelProperty(value = "样式属性（其他样式扩展）")
    private String cssClass;

    @ApiModelProperty(value = "表格字典样式")
    private String listClass;

    @ApiModelProperty(value = "是否默认（Y是 N否）")
    private String isDefault;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    private String status;
}
