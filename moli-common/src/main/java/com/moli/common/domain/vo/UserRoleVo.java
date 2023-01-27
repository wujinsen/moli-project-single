package com.moli.common.domain.vo;

import com.moli.common.domain.entity.SysRole;
import com.moli.common.domain.entity.SysUser;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleVo {

    private SysUser user;

    private Long UserId;

    private List<SysRole> roleList;

    private List<Long> roleIds;

}
