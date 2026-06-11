package com.moli.api;

import com.moli.common.domain.entity.SysPost;
import com.moli.common.domain.vo.PostVo;
import com.moli.system.controller.PostController;
import com.moli.system.mapper.PostMapper;
import com.moli.testsupport.AbstractApiTest;
import com.moli.testsupport.ControllerTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PostControllerApiTest extends AbstractApiTest {

    @InjectMocks
    private PostController controller;

    @Mock
    private PostMapper postMapper;

    @Test
    public void GET_post_list() {
        ControllerTestSupport.stubEmptyPage(postMapper);
        PostVo vo = new PostVo();
        vo.setPageNum(1);
        vo.setPageSize(10);
        ControllerTestSupport.assertSuccess(controller.list(vo));
    }

    @Test
    public void POST_post_insert() {
        ControllerTestSupport.stubInsert(postMapper);
        ControllerTestSupport.assertSuccess(controller.insert(new SysPost()));
    }

    @Test
    public void PUT_post_update() {
        ControllerTestSupport.stubUpdate(postMapper);
        ControllerTestSupport.assertSuccess(controller.update(new SysPost()));
    }

    @Test
    public void GET_post_id() {
        ControllerTestSupport.stubSelectById(postMapper, new SysPost());
        ControllerTestSupport.assertSuccess(controller.selectOne(1L));
    }

    @Test
    public void DELETE_post_ids() {
        ControllerTestSupport.assertSuccess(controller.remove(new Long[]{1L}));
    }

    @Test
    public void GET_post_allPost() {
        ControllerTestSupport.stubSelectListEmpty(postMapper);
        ControllerTestSupport.assertSuccess(controller.allPost());
    }
}
