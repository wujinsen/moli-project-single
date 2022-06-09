package com.moli.common.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
public class Dept{

    @ApiModelProperty("ID")
    @TableField(fill = FieldFill.INSERT)
    private Long id;

    @ApiModelProperty("父级id")
    private Long parentId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("排序号")
    private Integer orderNum;

    @ApiModelProperty(value = "1:正常 0:停用")
    private Integer status;

    @ApiModelProperty("创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createId;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("修改人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("修改时间")
    private Date updateTime;


}
