package com.moli.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.moli.common.domain.entity.Post;
import com.moli.system.mapper.PostMapper;
import com.moli.system.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;

@Service
public class PostServiceImpl implements PostService {

}
