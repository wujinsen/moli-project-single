package com.moli.common.constant;

import org.junit.Assert;
import org.junit.Test;

public class SystemGroupConstantTest {

    @Test
    public void isValid_acceptsAllPortalGroups() {
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.PLATFORM));
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.BUSINESS));
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.DATA));
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.TECH));
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.OPS));
    }

    @Test
    public void isValid_rejectsUnknownOrBlank() {
        Assert.assertFalse(SystemGroupConstant.isValid(null));
        Assert.assertFalse(SystemGroupConstant.isValid(""));
        Assert.assertFalse(SystemGroupConstant.isValid("unknown"));
        Assert.assertFalse(SystemGroupConstant.isValid("devops"));
        Assert.assertFalse(SystemGroupConstant.isValid("governance"));
        Assert.assertFalse(SystemGroupConstant.isValid("ai"));
        Assert.assertFalse(SystemGroupConstant.isValid("office"));
    }

    @Test
    public void normalize_returnsInputWhenValid() {
        Assert.assertEquals(SystemGroupConstant.TECH, SystemGroupConstant.normalize("tech"));
        Assert.assertEquals(SystemGroupConstant.PLATFORM, SystemGroupConstant.normalize("platform"));
    }

    @Test
    public void normalize_fallsBackToBusinessDefault() {
        Assert.assertEquals(SystemGroupConstant.DEFAULT, SystemGroupConstant.normalize(null));
        Assert.assertEquals(SystemGroupConstant.DEFAULT, SystemGroupConstant.normalize(""));
        Assert.assertEquals(SystemGroupConstant.DEFAULT, SystemGroupConstant.normalize("invalid"));
        Assert.assertEquals(SystemGroupConstant.BUSINESS, SystemGroupConstant.DEFAULT);
    }
}
