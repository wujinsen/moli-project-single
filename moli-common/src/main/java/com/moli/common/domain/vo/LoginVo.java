package com.moli.common.domain.vo;

import com.moli.common.domain.entity.SysUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class LoginVo {

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "用户信息")
    private SysUser user;

    @ApiModelProperty(value = "菜单树")
    private List<MenuVo> menuVoList;

}
