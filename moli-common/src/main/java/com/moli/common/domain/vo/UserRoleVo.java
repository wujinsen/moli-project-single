package com.moli.common.domain.vo;

import com.moli.common.domain.entity.Role;
import com.moli.common.domain.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleVo {

    private User user;

    private Long UserId;

    private List<Role> roleList;

}
