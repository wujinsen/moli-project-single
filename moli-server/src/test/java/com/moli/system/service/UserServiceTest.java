package com.moli.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moli.common.core.IdGenerator;
import com.moli.common.domain.entity.SysUser;
import com.moli.system.mapper.DeptMapper;
import com.moli.system.mapper.SysUserMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.tools.tree.ShiftLeftExpression;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserServiceTest {

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private DeptMapper deptMapper;

    @Resource
    private SysUserMapper userMapper;

    @Test
    public void test() {
        System.out.println(IdGenerator.getId());
    }

    @Test
    public void aaa() {
        System.out.println("====: " + deptMapper.aaa());
    }
    @Test
    public void AllofTest() throws  Exception{
        Long startTime = System.currentTimeMillis();
        // 异步执行每个数据源查询方法
        // 返回一个Future集合
        List<CompletableFuture<List<SysUser>>> futures = new ArrayList<>();
        for(int i=0;i<3;i++){
            futures.add(queryUsers());
            futures.add(queryUsers());
        }
        System.out.println("长度: " + futures.size());
        // 多个异步执行结果合并到该集合
        List<SysUser> futureUsers = new ArrayList<>();

        // 通过allOf对多个异步执行结果进行处理
        CompletableFuture allFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .whenComplete((v, t) -> {
                    // 所有CompletableFuture执行完成后进行遍历
                    futures.forEach(future -> {
                        synchronized (this) {
                            // 查询结果合并
                            futureUsers.addAll(future.getNow(null));
                        }
                    });
                });


        // 阻塞等待所有CompletableFuture执行完成
        allFuture.get();
        // 对合并后的结果集进行去重处理
        List<SysUser> result = futureUsers.stream().collect(Collectors.toList());
        Long endTime = System.currentTimeMillis();

        log.info("result: {}, time: {}", result.size(), (endTime - startTime));
    }

    /**
     * 用户异步查询方法
     * @return
     */
    public CompletableFuture<List<SysUser>> queryUsers() throws  Exception {

        // 定义异步查询Future对象
        CompletableFuture<List<SysUser>> queryFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return userMapper.selectList(new LambdaQueryWrapper<>());
        });

        // 返回future对象
        return queryFuture;
    }

}
