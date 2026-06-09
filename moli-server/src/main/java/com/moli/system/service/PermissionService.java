package com.moli.system.service;

import java.util.Set;

public interface PermissionService {

    Set<String> getPermissionsByUserId(Long userId, String userName);
}
