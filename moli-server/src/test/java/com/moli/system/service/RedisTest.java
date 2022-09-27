package com.moli.system.service;

import com.moli.system.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static java.math.BigDecimal.ROUND_UP;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedisTest {
    @Resource
    private PostService postService;

    @Resource
    private RedisTemplate redisTemplate;


    @Test
    public void test() {
        BigDecimal chatHot = new BigDecimal(9);
        BigDecimal reduceChatHot = chatHot.multiply(new BigDecimal(0.1));//.setScale(2, ROUND_HALF_UP);
        System.out.println(reduceChatHot.setScale(0, ROUND_UP).intValue());
        reduceChatHot = new BigDecimal(0.1);
        System.out.println(reduceChatHot);
        System.out.println(reduceChatHot.setScale(0, ROUND_UP).intValue());
        redisTemplate.opsForHash().increment("room:hot:1", "join", 18);
        //   redisTemplate.opsForHash().increment("room:hot:1", "chat", 18);
        redisTemplate.opsForHash().increment("room:hot:2", "chat", 18);
        // redisTemplate.opsForHash().put("aaa", "lisi", 15);
        System.out.println(redisTemplate.opsForHash().values("aaa"));
        Cursor<Map.Entry<String, Object>> cursor = redisTemplate.opsForHash().scan("room:hot:1", ScanOptions.NONE);
        Map map = redisTemplate.opsForHash().entries("room:hot:1");
        if (map.containsKey("chat")) {
            redisTemplate.opsForHash().increment("room:hot:1", "chat", -10);
        }
    }

    @Test
    public void test2() {
        BoundZSetOperations<String, String> boundZSetOperations = redisTemplate.boundZSetOps("class:100");
        redisTemplate.opsForZSet().add("class:100", "zhaoqi", System.currentTimeMillis());
        Set set = redisTemplate.opsForZSet().range("class:100", 0, -1);


        Cursor<ZSetOperations.TypedTuple<String>> scan = redisTemplate.boundZSetOps("class:100").scan(ScanOptions.NONE);
        // 需要迭代
        while (scan.hasNext()) {
            ZSetOperations.TypedTuple<String> next = scan.next();
            System.out.println("value:" + next.getValue() + "," + "score:" + next.getScore().longValue());
        }
        // boundZSetOperations.incrementScore("wangwu", 3000);
        // boundZSetOperations.range(0,-1).forEach(m -> System.out.println("获取map键值对:" + m ));
        // boundHashOperations.entries().forEach((m,n) -> System.out.println("获取map键值对:" + m + "-" + n));
    }

    @Test
    public void test3() {

        for(int i=0; i<10000; i++){
            redisTemplate.opsForValue().set("bbb"+i, i);
        }
        for(int i=0; i<10000; i++){
            redisTemplate.opsForValue().set("aaa"+i, i);
        }
        for(int i=0; i<10000; i++){
            redisTemplate.opsForValue().set("ccc"+i, i);
        }
//        Long start = System.currentTimeMillis();
//        Set set = redisTemplate.keys("*");
//
//        Long end = System.currentTimeMillis();
//        System.out.println("耗时: " + (end-start) + "毫秒" + set.size());

    }
}
