package com.moli.config.util;


import com.moli.common.constant.RedisConstant;
import com.moli.common.constant.ShiroSessionConstant;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.core.env.Environment;

import java.util.Set;

@Slf4j
public class ShiroUtils {

    private static RedisSessionDAO redisSessionDAO;

    private ShiroUtils() {
    }

    private static RedisSessionDAO getRedisSessionDAO() {
        if (redisSessionDAO == null) {
            redisSessionDAO = SpringUtil.getBean(RedisSessionDAO.class);
        }
        return redisSessionDAO;
    }

    private static boolean isSingleSession() {
        try {
            Environment env = SpringUtil.getBean(Environment.class);
            return Boolean.TRUE.equals(env.getProperty("shiro.single-session", Boolean.class, false));
        } catch (Exception e) {
            return false;
        }
    }

    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    public static void logout() {
        SysUser user = getUserInfo();
        if (user != null) {
            removeCurrentUserSessionIndex(user.getUserName());
        }
        SecurityUtils.getSubject().logout();
    }

    public static SysUser getUserInfo() {
        return (SysUser) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 登录成功后记录 Session，供单端踢下线或停用用户时清理。
     */
    public static void bindUserSession(String userName) {
        if (userName == null || userName.isEmpty()) {
            return;
        }
        try {
            Session session = getSession();
            if (session == null || session.getId() == null) {
                return;
            }
            RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
            String sessionId = session.getId().toString();
            long ttlSeconds = session.getTimeout() > 0
                    ? session.getTimeout() / 1000
                    : RedisConstant.REDIS_EXPIRE_TWO;
            if (isSingleSession()) {
                String indexKey = String.format(RedisConstant.SHIRO_USER_SESSION_KEY, userName);
                redisUtil.set(indexKey, sessionId, ttlSeconds);
            } else {
                String sessionsKey = String.format(RedisConstant.SHIRO_USER_SESSIONS_KEY, userName);
                redisUtil.sSet(sessionsKey, sessionId);
                redisUtil.expire(sessionsKey, ttlSeconds);
            }
        } catch (Exception e) {
            log.warn("Failed to bind shiro session index for user [{}]: {}", userName, e.getMessage());
        }
    }

    /**
     * 删除用户缓存与 Session。Redis 异常时不阻断登录流程。
     */
    public static void deleteCache(String userName, boolean isRemoveSession) {
        if (userName == null || userName.isEmpty()) {
            return;
        }
        try {
            if (isSingleSession()) {
                deleteSingleSession(userName, isRemoveSession);
            } else {
                deleteAllUserSessions(userName, isRemoveSession);
            }
        } catch (Exception e) {
            log.warn("Failed to delete shiro cache for user [{}], skipped: {}", userName, e.getMessage());
        }
    }

    private static void deleteSingleSession(String userName, boolean isRemoveSession) {
        RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
        String indexKey = String.format(RedisConstant.SHIRO_USER_SESSION_KEY, userName);
        Object sessionIdObj = redisUtil.get(indexKey);
        if (sessionIdObj == null) {
            return;
        }
        removeSessionById(sessionIdObj.toString(), isRemoveSession);
        redisUtil.del(indexKey);
    }

    private static void deleteAllUserSessions(String userName, boolean isRemoveSession) {
        RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
        String sessionsKey = String.format(RedisConstant.SHIRO_USER_SESSIONS_KEY, userName);
        Set<Object> sessionIds = redisUtil.sGet(sessionsKey);
        if (sessionIds == null || sessionIds.isEmpty()) {
            redisUtil.del(sessionsKey);
            return;
        }
        for (Object sessionIdObj : sessionIds) {
            if (sessionIdObj != null) {
                removeSessionById(sessionIdObj.toString(), isRemoveSession);
            }
        }
        redisUtil.del(sessionsKey);
    }

    private static void removeSessionById(String sessionId, boolean isRemoveSession) {
        Session session = getRedisSessionDAO().readSession(sessionId);
        if (session == null) {
            return;
        }
        Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        if (isRemoveSession) {
            getRedisSessionDAO().delete(session);
        }
        if (attribute != null) {
            DefaultWebSecurityManager securityManager =
                    (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
            Authenticator authc = securityManager.getAuthenticator();
            ((LogoutAware) authc).onLogout((SimplePrincipalCollection) attribute);
        }
    }

    public static void setCurrentSystem(Long systemId, String systemCode) {
        Session session = getSession();
        if (session == null) {
            return;
        }
        if (systemId == null) {
            session.removeAttribute(ShiroSessionConstant.CURRENT_SYSTEM_ID);
            session.removeAttribute(ShiroSessionConstant.CURRENT_SYSTEM_CODE);
            return;
        }
        session.setAttribute(ShiroSessionConstant.CURRENT_SYSTEM_ID, systemId);
        session.setAttribute(ShiroSessionConstant.CURRENT_SYSTEM_CODE, systemCode);
    }

    public static Long getCurrentSystemId() {
        Session session = getSession();
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(ShiroSessionConstant.CURRENT_SYSTEM_ID);
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static void removeCurrentUserSessionIndex(String userName) {
        try {
            Session session = getSession();
            if (session == null || session.getId() == null) {
                return;
            }
            RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
            String sessionId = session.getId().toString();
            if (isSingleSession()) {
                String indexKey = String.format(RedisConstant.SHIRO_USER_SESSION_KEY, userName);
                Object indexed = redisUtil.get(indexKey);
                if (indexed != null && sessionId.equals(indexed.toString())) {
                    redisUtil.del(indexKey);
                }
            } else {
                String sessionsKey = String.format(RedisConstant.SHIRO_USER_SESSIONS_KEY, userName);
                redisUtil.setRemove(sessionsKey, sessionId);
            }
        } catch (Exception e) {
            log.warn("Failed to clear shiro session index for user [{}]: {}", userName, e.getMessage());
        }
    }

}
