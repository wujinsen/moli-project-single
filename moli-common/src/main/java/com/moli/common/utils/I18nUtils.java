package com.moli.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Language resolver for zh-CN, en-US and ja-JP.
 */
public final class I18nUtils {

    public static final String DEFAULT_LANG = "zh-CN";
    public static final String LANG_EN = "en-US";
    public static final String LANG_JA = "ja-JP";

    private I18nUtils() {
    }

    public static boolean isSupported(String lang) {
        return DEFAULT_LANG.equals(lang) || LANG_EN.equals(lang) || LANG_JA.equals(lang);
    }

    public static String resolveLanguage() {
        try {
            if (ServletUtils.getRequest() != null) {
                String header = ServletUtils.getRequest().getHeader("Accept-Language");
                if (StringUtils.isNotBlank(header)) {
                    String lang = header.split(",")[0].trim();
                    if (isSupported(lang)) {
                        return lang;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return DEFAULT_LANG;
    }

    public static String resolveLanguage(String preferred) {
        if (isSupported(preferred)) {
            return preferred;
        }
        return resolveLanguage();
    }

    public static String resolveLocalizedText(String zh, String en, String ja, String lang) {
        if (LANG_EN.equals(lang) && StringUtils.isNotBlank(en)) {
            return en;
        }
        if (LANG_JA.equals(lang) && StringUtils.isNotBlank(ja)) {
            return ja;
        }
        if (StringUtils.isNotBlank(zh)) {
            return zh;
        }
        if (StringUtils.isNotBlank(en)) {
            return en;
        }
        return ja;
    }
}
