package com.moli.common.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
public class Dept{

    @ApiModelProperty("IDs")
    @TableField(fill = FieldFill.INSERT)
    private Long id;

    @ApiModelProperty("创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("修改人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("修改时间")
    private Date updateTime;

    private Long parentId;

    private String deptName;

    private Integer orderNum;

}
