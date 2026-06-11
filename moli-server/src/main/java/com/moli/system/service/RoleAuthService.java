package com.moli.system.service;

import com.moli.common.domain.vo.RoleAuthVo;

import java.util.List;

public interface RoleAuthService {

    void assignRoleAuth(Long roleId, List<Long> menuIds, List<String> actionCodes);

    RoleAuthVo getRoleAuth(Long roleId);
}
