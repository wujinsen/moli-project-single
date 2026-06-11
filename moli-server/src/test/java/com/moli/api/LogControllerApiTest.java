package com.moli.api;

import com.moli.common.domain.entity.SysLoginLog;
import com.moli.common.domain.entity.SysOperationLog;
import com.moli.system.controller.LogController;
import com.moli.system.mapper.SysLoginLogMapper;
import com.moli.system.mapper.SysOperationLogMapper;
import com.moli.testsupport.AbstractApiTest;
import com.moli.testsupport.ControllerTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogControllerApiTest extends AbstractApiTest {

    @InjectMocks
    private LogController controller;

    @Mock
    private SysLoginLogMapper sysLoginLogMapper;

    @Mock
    private SysOperationLogMapper sysOperationLogMapper;

    @Test
    public void GET_log_loginLogList() {
        ControllerTestSupport.stubEmptyPage(sysLoginLogMapper);
        SysLoginLog req = new SysLoginLog();
        req.setPageNum(1);
        req.setPageSize(10);
        ControllerTestSupport.assertSuccess(controller.loginLogList(req));
    }

    @Test
    public void DELETE_log_loginLog_ids() {
        ControllerTestSupport.assertSuccess(controller.deleteLoginLog("1,2"));
    }

    @Test
    public void DELETE_log_loginLog_clean() {
        ControllerTestSupport.assertSuccess(controller.cleanLoginLog());
    }

    @Test
    public void GET_log_operationLogList() {
        ControllerTestSupport.stubEmptyPage(sysOperationLogMapper);
        SysOperationLog req = new SysOperationLog();
        req.setPageNum(1);
        req.setPageSize(10);
        ControllerTestSupport.assertSuccess(controller.operationLogList(req));
    }

    @Test
    public void DELETE_log_operationLog_ids() {
        ControllerTestSupport.assertSuccess(controller.deleteOperationLog("1"));
    }

    @Test
    public void DELETE_log_operationLog_clean() {
        ControllerTestSupport.assertSuccess(controller.cleanOperationLog());
    }
}
