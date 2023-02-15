package com.moli.common.domain.vo;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class UserVo extends BaseEntity {

    @NotNull(message = "部门ID不能为空")
    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = " 工号")
    private String workNo;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "身份证号码")
    private String identityCard;

    @ApiModelProperty(value = "性别")
    private String sex;

    @NotNull(message = "手机号不能为空")
    @ApiModelProperty(value = "电话号(唯一)")
    private String telephone;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "入职日期")
    private Date workTime;

    @ApiModelProperty(value = "是否在职(0-在职；1-离职)")
    private Integer isJob;

    @ApiModelProperty(value = "是否锁定(0-未锁；1-已锁)")
    private Integer status;

    @ApiModelProperty(value = "密码错误数量")
    private Integer errorNum;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "初始密码")
    private String initPassword;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "岗位id")
    private List<Long> postIds;

    @ApiModelProperty(value = "角色id集合")
    private List<Long> roleIds;

    @ApiModelProperty(value = "角色id")
    private Long roleId;

}
