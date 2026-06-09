package com.moli.common.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 从 User-Agent 解析浏览器与操作系统信息。
 */
public final class UserAgentUtils {

    private UserAgentUtils() {
    }

    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String userAgent = request.getHeader("User-Agent");
        return userAgent == null ? "" : userAgent;
    }

    public static String getBrowser(HttpServletRequest request) {
        return getBrowser(getUserAgent(request));
    }

    public static String getOs(HttpServletRequest request) {
        return getOs(getUserAgent(request));
    }

    public static String getBrowser(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "未知";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("edg/") || ua.contains("edge/")) {
            return "Edge";
        }
        if (ua.contains("chrome") || ua.contains("crios")) {
            return "Chrome";
        }
        if (ua.contains("firefox") || ua.contains("fxios")) {
            return "Firefox";
        }
        if (ua.contains("safari") && !ua.contains("chrome") && !ua.contains("crios")) {
            return "Safari";
        }
        if (ua.contains("msie") || ua.contains("trident")) {
            return "IE";
        }
        if (ua.contains("opera") || ua.contains("opr/")) {
            return "Opera";
        }
        return "未知";
    }

    public static String getOs(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "未知";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows nt 10")) {
            return "Windows 10";
        }
        if (ua.contains("windows nt 6.3")) {
            return "Windows 8.1";
        }
        if (ua.contains("windows nt 6.2")) {
            return "Windows 8";
        }
        if (ua.contains("windows nt 6.1")) {
            return "Windows 7";
        }
        if (ua.contains("windows")) {
            return "Windows";
        }
        if (ua.contains("android")) {
            return "Android";
        }
        if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ios")) {
            return "iOS";
        }
        if (ua.contains("mac os x") || ua.contains("macintosh")) {
            return "Mac OS X";
        }
        if (ua.contains("linux")) {
            return "Linux";
        }
        return "未知";
    }

}
