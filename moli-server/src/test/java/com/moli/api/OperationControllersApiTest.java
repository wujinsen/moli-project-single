package com.moli.api;

import com.moli.common.domain.entity.OperationComponentDeployInfo;
import com.moli.common.domain.entity.OperationPlatformInfo;
import com.moli.common.domain.entity.OperationProjectDeployInfo;
import com.moli.common.domain.entity.OperationServerInfo;
import com.moli.common.domain.vo.OperationServerInfoVo;
import com.moli.operation.controller.OperationComponentController;
import com.moli.operation.controller.OperationPlatformController;
import com.moli.operation.controller.OperationProjectController;
import com.moli.operation.controller.OperationServerController;
import com.moli.operation.mapper.OperationComponentDeployInfoMapper;
import com.moli.operation.mapper.OperationPlatformMapper;
import com.moli.operation.mapper.OperationProjectDeployInfoMapper;
import com.moli.operation.mapper.OperationServerMapper;
import com.moli.testsupport.AbstractApiTest;
import com.moli.testsupport.ControllerTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OperationControllersApiTest extends AbstractApiTest {

    @InjectMocks
    private OperationPlatformController platformController;
    @InjectMocks
    private OperationServerController serverController;
    @InjectMocks
    private OperationProjectController projectController;
    @InjectMocks
    private OperationComponentController componentController;

    @Mock
    private OperationPlatformMapper operationPlatformMapper;
    @Mock
    private OperationServerMapper operationServerMapper;
    @Mock
    private OperationProjectDeployInfoMapper operationProjectDeployInfoMapper;
    @Mock
    private OperationComponentDeployInfoMapper operationComponentDeployInfoMapper;

    @Test
    public void GET_operation_platform_list() {
        ControllerTestSupport.stubEmptyPage(operationPlatformMapper);
        OperationPlatformInfo q = new OperationPlatformInfo();
        q.setPageNum(1);
        q.setPageSize(10);
        ControllerTestSupport.assertSuccess(platformController.list(q));
    }

    @Test
    public void POST_operation_platform_insert() {
        ControllerTestSupport.stubInsert(operationPlatformMapper);
        ControllerTestSupport.assertSuccess(platformController.insert(new OperationPlatformInfo()));
    }

    @Test
    public void PUT_operation_platform_update() {
        ControllerTestSupport.stubUpdate(operationPlatformMapper);
        ControllerTestSupport.assertSuccess(platformController.update(new OperationPlatformInfo()));
    }

    @Test
    public void GET_operation_platform_id() {
        ControllerTestSupport.stubSelectById(operationPlatformMapper, new OperationPlatformInfo());
        ControllerTestSupport.assertSuccess(platformController.selectOne(1L));
    }

    @Test
    public void DELETE_operation_platform_ids() {
        ControllerTestSupport.assertSuccess(platformController.remove(new Long[]{1L}));
    }

    @Test
    public void GET_operation_server_list() {
        ControllerTestSupport.stubEmptyPage(operationServerMapper);
        OperationServerInfoVo q = new OperationServerInfoVo();
        q.setPageNum(1);
        q.setPageSize(10);
        ControllerTestSupport.assertSuccess(serverController.list(q));
    }

    @Test
    public void POST_operation_server_insert() {
        ControllerTestSupport.stubInsert(operationServerMapper);
        ControllerTestSupport.assertSuccess(serverController.insert(new OperationServerInfo()));
    }

    @Test
    public void PUT_operation_server_update() {
        ControllerTestSupport.stubUpdate(operationServerMapper);
        ControllerTestSupport.assertSuccess(serverController.update(new OperationServerInfo()));
    }

    @Test
    public void GET_operation_server_id() {
        ControllerTestSupport.stubSelectById(operationServerMapper, new OperationServerInfo());
        ControllerTestSupport.assertSuccess(serverController.selectOne(1L));
    }

    @Test
    public void DELETE_operation_server_ids() {
        ControllerTestSupport.assertSuccess(serverController.remove(new Long[]{1L}));
    }

    @Test
    public void GET_operation_project_list() {
        ControllerTestSupport.stubEmptyPage(operationProjectDeployInfoMapper);
        OperationProjectDeployInfo q = new OperationProjectDeployInfo();
        q.setPageNum(1);
        q.setPageSize(10);
        ControllerTestSupport.assertSuccess(projectController.list(q));
    }

    @Test
    public void POST_operation_project_insert() {
        ControllerTestSupport.stubInsert(operationProjectDeployInfoMapper);
        ControllerTestSupport.assertSuccess(projectController.insert(new OperationProjectDeployInfo()));
    }

    @Test
    public void PUT_operation_project_update() {
        ControllerTestSupport.stubUpdate(operationProjectDeployInfoMapper);
        ControllerTestSupport.assertSuccess(projectController.update(new OperationProjectDeployInfo()));
    }

    @Test
    public void GET_operation_project_id() {
        ControllerTestSupport.stubSelectById(operationProjectDeployInfoMapper, new OperationProjectDeployInfo());
        ControllerTestSupport.assertSuccess(projectController.selectOne(1L));
    }

    @Test
    public void DELETE_operation_project_ids() {
        ControllerTestSupport.assertSuccess(projectController.remove(new Long[]{1L}));
    }

    @Test
    public void GET_operation_component_list() {
        ControllerTestSupport.stubEmptyPage(operationComponentDeployInfoMapper);
        OperationComponentDeployInfo q = new OperationComponentDeployInfo();
        q.setPageNum(1);
        q.setPageSize(10);
        ControllerTestSupport.assertSuccess(componentController.list(q));
    }

    @Test
    public void POST_operation_component_insert() {
        ControllerTestSupport.stubInsert(operationComponentDeployInfoMapper);
        ControllerTestSupport.assertSuccess(componentController.insert(new OperationComponentDeployInfo()));
    }

    @Test
    public void PUT_operation_component_update() {
        ControllerTestSupport.stubUpdate(operationComponentDeployInfoMapper);
        ControllerTestSupport.assertSuccess(componentController.update(new OperationComponentDeployInfo()));
    }

    @Test
    public void GET_operation_component_id() {
        ControllerTestSupport.stubSelectById(operationComponentDeployInfoMapper, new OperationComponentDeployInfo());
        ControllerTestSupport.assertSuccess(componentController.selectOne(1L));
    }

    @Test
    public void DELETE_operation_component_ids() {
        ControllerTestSupport.assertSuccess(componentController.remove(new Long[]{1L}));
    }
}
