package com.moli.testsupport;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.core.MoliResult;
import org.junit.Assert;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public final class ControllerTestSupport {

    static {
        MybatisPlusTestSupport.initAll();
    }

    private ControllerTestSupport() {
    }

    public static void assertSuccess(MoliResult<?> result) {
        Assert.assertNotNull(result);
        Assert.assertEquals(200, result.getCode());
    }

    @SuppressWarnings("unchecked")
    public static <T> void stubEmptyPage(BaseMapper<T> mapper) {
        when(mapper.selectPage(any(Page.class), any())).thenAnswer(invocation -> {
            Page<T> page = invocation.getArgument(0);
            page.setRecords(Collections.emptyList());
            page.setTotal(0);
            return page;
        });
    }

    public static void stubInsert(BaseMapper<?> mapper) {
        when(mapper.insert(any())).thenReturn(1);
    }

    public static void stubUpdate(BaseMapper<?> mapper) {
        when(mapper.updateById(any())).thenReturn(1);
    }

    public static void stubSelectById(BaseMapper<?> mapper, Object entity) {
        when(mapper.selectById(any())).thenReturn(entity);
    }

    public static void stubSelectListEmpty(BaseMapper<?> mapper) {
        when(mapper.selectList(any())).thenReturn(Collections.emptyList());
    }
}
