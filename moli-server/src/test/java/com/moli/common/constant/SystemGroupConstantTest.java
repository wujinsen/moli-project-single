package com.moli.common.constant;

import org.junit.Assert;
import org.junit.Test;

public class SystemGroupConstantTest {

    @Test
    public void isValid_acceptsAllPortalGroups() {
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.GOVERNANCE));
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.BUSINESS));
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.AI));
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.TECH));
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.OPS));
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.DATA));
        Assert.assertTrue(SystemGroupConstant.isValid(SystemGroupConstant.OFFICE));
    }

    @Test
    public void isValid_rejectsUnknownOrBlank() {
        Assert.assertFalse(SystemGroupConstant.isValid(null));
        Assert.assertFalse(SystemGroupConstant.isValid(""));
        Assert.assertFalse(SystemGroupConstant.isValid("unknown"));
        Assert.assertFalse(SystemGroupConstant.isValid("devops"));
    }

    @Test
    public void normalize_returnsInputWhenValid() {
        Assert.assertEquals(SystemGroupConstant.AI, SystemGroupConstant.normalize("ai"));
        Assert.assertEquals(SystemGroupConstant.GOVERNANCE, SystemGroupConstant.normalize("governance"));
    }

    @Test
    public void normalize_fallsBackToBusinessDefault() {
        Assert.assertEquals(SystemGroupConstant.DEFAULT, SystemGroupConstant.normalize(null));
        Assert.assertEquals(SystemGroupConstant.DEFAULT, SystemGroupConstant.normalize(""));
        Assert.assertEquals(SystemGroupConstant.DEFAULT, SystemGroupConstant.normalize("invalid"));
        Assert.assertEquals(SystemGroupConstant.BUSINESS, SystemGroupConstant.DEFAULT);
    }
}
