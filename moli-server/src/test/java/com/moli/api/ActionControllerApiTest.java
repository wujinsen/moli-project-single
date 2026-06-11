package com.moli.api;

import com.moli.common.domain.entity.SysAction;
import com.moli.common.domain.vo.ActionQueryVo;
import com.moli.common.page.PageRes;
import com.moli.system.controller.ActionController;
import com.moli.system.service.ActionService;
import com.moli.testsupport.AbstractApiTest;
import com.moli.testsupport.ControllerTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActionControllerApiTest extends AbstractApiTest {

    @InjectMocks
    private ActionController controller;

    @Mock
    private ActionService actionService;

    @Test
    public void GET_action_list_by_menu() {
        when(actionService.listByMenuId(1L)).thenReturn(Collections.emptyList());
        ControllerTestSupport.assertSuccess(controller.list(1L));
    }

    @Test
    public void GET_action_page() {
        PageRes<com.moli.common.domain.vo.ActionVo> page = new PageRes<>();
        page.setList(Collections.emptyList());
        page.setTotal(0);
        when(actionService.page(any(ActionQueryVo.class))).thenReturn(page);
        ControllerTestSupport.assertSuccess(controller.page(new ActionQueryVo()));
    }

    @Test
    public void POST_action_insert() {
        when(actionService.save(any(SysAction.class))).thenReturn(true);
        ControllerTestSupport.assertSuccess(controller.insert(new SysAction()));
    }

    @Test
    public void PUT_action_update() {
        when(actionService.update(any(SysAction.class))).thenReturn(true);
        ControllerTestSupport.assertSuccess(controller.update(new SysAction()));
    }

    @Test
    public void DELETE_action_ids() {
        when(actionService.removeByIds(any())).thenReturn(true);
        ControllerTestSupport.assertSuccess(controller.remove("1,2"));
    }

    @Test
    public void GET_action_id() {
        when(actionService.getById(1L)).thenReturn(null);
        ControllerTestSupport.assertSuccess(controller.getInfo(1L));
    }

    @Test
    public void PUT_action_changeStatus() {
        SysAction action = new SysAction();
        action.setId(1L);
        action.setStatus(0);
        when(actionService.changeStatus(eq(1L), eq(0))).thenReturn(true);
        ControllerTestSupport.assertSuccess(controller.changeStatus(action));
    }
}
