package com.moli.common.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * SSO 门户选系统页分组（大厂业务域划分）。
 */
public final class SystemGroupConstant {

    private SystemGroupConstant() {
    }

    public static final String PLATFORM = "platform";
    public static final String BUSINESS = "business";
    public static final String DATA = "data";
    public static final String TECH = "tech";
    public static final String OPS = "ops";

    public static final String DEFAULT = BUSINESS;

    private static final Set<String> ALLOWED = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            PLATFORM, BUSINESS, DATA, TECH, OPS
    )));

    public static boolean isValid(String group) {
        return group != null && ALLOWED.contains(group);
    }

    public static String normalize(String group) {
        if (isValid(group)) {
            return group;
        }
        return DEFAULT;
    }
}
