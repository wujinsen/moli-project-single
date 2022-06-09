package com.moli.service;


import com.moli.system.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PostServiceTest {
    @Resource
    private PostService postService;

    @Test
    public void test(){
        postService.test();
    }

}
