package com.moli.config.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;

@Slf4j
public final class PermissionAuthUtils {

    private PermissionAuthUtils() {
    }

    public static void clearUserAuthorizationCache(String userName) {
        if (StringUtils.isBlank(userName)) {
            return;
        }
        try {
            RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
            if (securityManager.getRealms().isEmpty()) {
                return;
            }
            AuthorizingRealm realm = (AuthorizingRealm) securityManager.getRealms().iterator().next();
            Cache<Object, AuthorizationInfo> cache = realm.getAuthorizationCache();
            if (cache != null) {
                cache.remove(userName);
            }
        } catch (Exception e) {
            log.warn("clear authorization cache failed for user [{}]: {}", userName, e.getMessage());
        }
    }
}
