package com.moli.api;

import com.moli.common.domain.entity.SysDept;
import com.moli.system.controller.DeptController;
import com.moli.system.mapper.DeptMapper;
import com.moli.system.service.DeptService;
import com.moli.testsupport.AbstractApiTest;
import com.moli.testsupport.ControllerTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeptControllerApiTest extends AbstractApiTest {

    @InjectMocks
    private DeptController controller;

    @Mock
    private DeptMapper deptMapper;

    @Mock
    private DeptService deptService;

    @Test
    public void GET_dept_list() {
        ControllerTestSupport.stubSelectListEmpty(deptMapper);
        ControllerTestSupport.assertSuccess(controller.list(new SysDept()));
    }

    @Test
    public void GET_dept_getDeptTreeList() {
        ControllerTestSupport.stubSelectListEmpty(deptMapper);
        ControllerTestSupport.assertSuccess(controller.getDeptTreeList());
    }

    @Test
    public void POST_dept_insert() {
        ControllerTestSupport.stubInsert(deptMapper);
        ControllerTestSupport.assertSuccess(controller.insert(new SysDept()));
    }

    @Test
    public void PUT_dept_update() {
        ControllerTestSupport.stubUpdate(deptMapper);
        ControllerTestSupport.assertSuccess(controller.update(new SysDept()));
    }

    @Test
    public void GET_dept_id() {
        ControllerTestSupport.stubSelectById(deptMapper, new SysDept());
        ControllerTestSupport.assertSuccess(controller.getInfo(1L));
    }

    @Test
    public void DELETE_dept_id() {
        when(deptService.deleteWithChildren(1L)).thenReturn(true);
        ControllerTestSupport.assertSuccess(controller.remove(1L));
    }
}
