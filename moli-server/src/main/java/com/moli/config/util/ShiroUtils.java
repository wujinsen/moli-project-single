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

@Slf4j
public class ShiroUtils {

    private static RedisSessionDAO redisSessionDAO = SpringUtil.getBean(RedisSessionDAO.class);

    private ShiroUtils() {
    }

    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    public static void logout() {
        SysUser user = getUserInfo();
        if (user != null) {
            clearUserSessionIndex(user.getUserName());
        }
        SecurityUtils.getSubject().logout();
    }

    public static SysUser getUserInfo() {
        return (SysUser) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 登录成功后绑定用户名与 Session，供下次登录时定点清理旧会话。
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
            String indexKey = String.format(RedisConstant.SHIRO_USER_SESSION_KEY, userName);
            long ttlSeconds = session.getTimeout() > 0
                    ? session.getTimeout() / 1000
                    : RedisConstant.REDIS_EXPIRE_TWO;
            redisUtil.set(indexKey, session.getId().toString(), ttlSeconds);
        } catch (Exception e) {
            log.warn("Failed to bind shiro session index for user [{}]: {}", userName, e.getMessage());
        }
    }

    /**
     * 删除用户缓存与旧 Session。Redis 异常时不阻断登录流程。
     */
    public static void deleteCache(String userName, boolean isRemoveSession) {
        if (userName == null || userName.isEmpty()) {
            return;
        }
        try {
            RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
            String indexKey = String.format(RedisConstant.SHIRO_USER_SESSION_KEY, userName);
            Object sessionIdObj = redisUtil.get(indexKey);
            if (sessionIdObj == null) {
                return;
            }
            String sessionId = sessionIdObj.toString();
            Session session = redisSessionDAO.readSession(sessionId);
            if (session == null) {
                redisUtil.del(indexKey);
                return;
            }
            Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (isRemoveSession) {
                redisSessionDAO.delete(session);
            }
            if (attribute != null) {
                DefaultWebSecurityManager securityManager =
                        (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
                Authenticator authc = securityManager.getAuthenticator();
                ((LogoutAware) authc).onLogout((SimplePrincipalCollection) attribute);
            }
            redisUtil.del(indexKey);
        } catch (Exception e) {
            log.warn("Failed to delete shiro cache for user [{}], skipped: {}", userName, e.getMessage());
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

    private static void clearUserSessionIndex(String userName) {
        try {
            RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
            redisUtil.del(String.format(RedisConstant.SHIRO_USER_SESSION_KEY, userName));
        } catch (Exception e) {
            log.warn("Failed to clear shiro session index for user [{}]: {}", userName, e.getMessage());
        }
    }

}
