package com.moli.common.domain.entity;

import com.moli.common.core.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
public class User extends BaseEntity {

    private Long deptId;

    @ApiModelProperty(value = "工号")
    private String workNo;

    @ApiModelProperty(value = "姓名")
    private String nickName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "身份证号码")
    private String identityCard;

    @ApiModelProperty(value = "性别")
    private String sex;

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

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "盐值")
    private String salt;

    @ApiModelProperty(value = "初始密码")
    private String initPassword;


}
