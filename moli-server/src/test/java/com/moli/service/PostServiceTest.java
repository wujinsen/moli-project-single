package com.moli.service;


import com.alibaba.fastjson.JSON;
import com.moli.common.domain.entity.Post;
import com.moli.system.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ROUND_UP;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PostServiceTest {

}
