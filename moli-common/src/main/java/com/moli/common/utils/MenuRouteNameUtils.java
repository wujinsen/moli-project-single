package com.moli.common.utils;

import com.moli.common.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * 生成 Vue Router 的 name（getRouters 返回的 name 字段）。
 * 默认规则：path 首字母大写（user → User）。
 * 当 component 末级与上一级同名（如 system/system/index）时，使用 XxxRegistry 避免与父级目录 System 冲突。
 */
public final class MenuRouteNameUtils {

    private MenuRouteNameUtils() {
    }

    public static String resolve(String routeName, String path, String component, String menuType) {
        if (StringUtils.isNotBlank(routeName)) {
            return routeName.trim();
        }
        return generate(path, component, menuType);
    }

    public static String generate(String path, String component, String menuType) {
        if (!CommonConstant.TYPE_MENU.equals(menuType)) {
            return StringUtils.capitalize(StringUtils.defaultString(path));
        }
        if (StringUtils.isNotBlank(component) && component.endsWith("/index")) {
            String base = component.substring(0, component.length() - "/index".length());
            String[] parts = base.split("/");
            int len = 0;
            for (String part : parts) {
                if (StringUtils.isNotBlank(part)) {
                    len++;
                }
            }
            if (len >= 2) {
                String last = null;
                String prev = null;
                int seen = 0;
                for (String part : parts) {
                    if (StringUtils.isBlank(part)) {
                        continue;
                    }
                    prev = last;
                    last = part;
                    seen++;
                }
                if (last != null && last.equals(prev)) {
                    return StringUtils.capitalize(last) + "Registry";
                }
                if (last != null) {
                    return StringUtils.capitalize(last);
                }
            }
        }
        return StringUtils.capitalize(StringUtils.defaultString(path));
    }

}
