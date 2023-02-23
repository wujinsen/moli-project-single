package com.moli.system.service;


import com.moli.common.domain.entity.SysPost;
import com.moli.system.mapper.PostMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PostServiceTest {

    @Resource
    private PostMapper postMapper;

    @Test
    public void insertTest(){
        SysPost post = new SysPost();
       // post.setId(111l);
        post.setPostName("aaa");
        post.setCreateId(1l);
        int num = postMapper.insert(post);
        System.out.println(num);
        System.out.println(post);
    }

    @Test
    public void aaa(){
        System.out.println("aaa");
    }
}
