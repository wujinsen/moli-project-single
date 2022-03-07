package com.moli.common.domain.vo;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DictTypeVo extends BaseEntity {

    @ApiModelProperty(value = "字典主键")
    private Long id;

    @ApiModelProperty(value = "字典名称")
    private String dictName;

    @ApiModelProperty(value = "字典类型")
    private String dictType;

    @ApiModelProperty(value = "状态（1正常 0停用)")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "备注")
    private String remark;

}
