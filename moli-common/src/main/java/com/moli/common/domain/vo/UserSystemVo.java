package com.moli.common.domain.vo;

import com.moli.common.domain.entity.SysSystem;
import com.moli.common.domain.entity.SysUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserSystemVo {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户信息")
    private SysUser user;

    @ApiModelProperty("已选系统ID")
    private List<Long> systemIds;

    @ApiModelProperty("可选系统列表")
    private List<SysSystem> systemList;

}
