package com.moli.common.domain.vo;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RoleVo extends BaseEntity {


    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "排序")
    private String orderNum;

    @ApiModelProperty("角色状态（0正常 1停用）")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "菜单id集合")
    private List<Long> menuIds;

    @ApiModelProperty("动作权限码")
    private List<String> actionCodes;

}
