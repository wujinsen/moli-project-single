package com.moli.common.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
public class SysDept extends BaseEntity {

    @ApiModelProperty("父级id")
    private Long parentId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("排序号")
    private Integer orderNum;

    @ApiModelProperty(value = "1:正常 0:停用")
    private Integer status;

}
