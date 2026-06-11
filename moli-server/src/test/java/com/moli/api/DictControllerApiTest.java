package com.moli.api;

import com.moli.common.domain.entity.SysDictData;
import com.moli.common.domain.entity.SysDictType;
import com.moli.common.domain.vo.DictDataVo;
import com.moli.common.domain.vo.DictTypeVo;
import com.moli.system.controller.DictController;
import com.moli.system.mapper.DictDataMapper;
import com.moli.system.mapper.DictTypeMapper;
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
public class DictControllerApiTest extends AbstractApiTest {

    @InjectMocks
    private DictController controller;

    @Mock
    private DictDataMapper dictDataMapper;

    @Mock
    private DictTypeMapper dictTypeMapper;

    @Test
    public void GET_dict_type_list() {
        ControllerTestSupport.stubEmptyPage(dictTypeMapper);
        DictTypeVo vo = new DictTypeVo();
        vo.setPageNum(1);
        vo.setPageSize(10);
        ControllerTestSupport.assertSuccess(controller.list(vo));
    }

    @Test
    public void GET_dict_type_listAll() {
        ControllerTestSupport.stubSelectListEmpty(dictTypeMapper);
        ControllerTestSupport.assertSuccess(controller.listAll());
    }

    @Test
    public void POST_dict_type() {
        when(dictTypeMapper.insert(any())).thenReturn(1);
        ControllerTestSupport.assertSuccess(controller.insert(new SysDictType()));
    }

    @Test
    public void PUT_dict_type() {
        when(dictTypeMapper.updateById(any())).thenReturn(1);
        ControllerTestSupport.assertSuccess(controller.update(new SysDictType()));
    }

    @Test
    public void GET_dict_type_id() {
        ControllerTestSupport.stubSelectById(dictTypeMapper, new SysDictType());
        ControllerTestSupport.assertSuccess(controller.getDictTypeInfo(1L));
    }

    @Test
    public void DELETE_dict_type_ids() {
        ControllerTestSupport.assertSuccess(controller.delete(new Long[]{1L}));
    }

    @Test
    public void GET_dict_data_type() {
        ControllerTestSupport.stubSelectListEmpty(dictDataMapper);
        ControllerTestSupport.assertSuccess(controller.dictType("sys_status"));
    }

    @Test
    public void GET_dict_data_list() {
        ControllerTestSupport.stubEmptyPage(dictDataMapper);
        DictDataVo vo = new DictDataVo();
        vo.setPageNum(1);
        vo.setPageSize(10);
        vo.setDictType("sys_status");
        ControllerTestSupport.assertSuccess(controller.list(vo));
    }

    @Test
    public void GET_dict_data_id() {
        ControllerTestSupport.stubSelectById(dictDataMapper, new SysDictData());
        ControllerTestSupport.assertSuccess(controller.getDictDataInfo(1L));
    }

    @Test
    public void POST_dict_data() {
        when(dictDataMapper.insert(any())).thenReturn(1);
        ControllerTestSupport.assertSuccess(controller.insertData(new SysDictData()));
    }

    @Test
    public void PUT_dict_data() {
        when(dictDataMapper.updateById(any())).thenReturn(1);
        ControllerTestSupport.assertSuccess(controller.getDictDataInfo(new SysDictData()));
    }

    @Test
    public void DELETE_dict_data_ids() {
        ControllerTestSupport.assertSuccess(controller.deleteData(new Long[]{1L}));
    }
}
