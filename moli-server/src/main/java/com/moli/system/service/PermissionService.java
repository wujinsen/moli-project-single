package com.moli.system.service;

import com.moli.common.domain.vo.CapabilitiesVo;

import java.util.Set;

public interface PermissionService {

    Set<String> getPermissionsByUserId(Long userId, String userName);

    CapabilitiesVo buildCapabilities(Long userId, String userName);
}
