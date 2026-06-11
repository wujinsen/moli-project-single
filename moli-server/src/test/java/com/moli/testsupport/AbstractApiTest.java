package com.moli.testsupport;

import org.junit.BeforeClass;

public abstract class AbstractApiTest {

    @BeforeClass
    public static void initMybatisPlusMetadata() {
        MybatisPlusTestSupport.initAll();
    }
}
