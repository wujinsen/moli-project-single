package com.moli.common.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SysRoleVo {
    @ApiModelProperty("ID")
    @TableField(fill = FieldFill.INSERT)
    private Long id;

    @ApiModelProperty("创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("修改人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "排序")
    private String orderNum;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("角色状态（1正常 0停用）")
    private Integer status;

    private List<Long> menuIds;

    @ApiModelProperty("动作权限码")
    private List<String> actionCodes;

}
