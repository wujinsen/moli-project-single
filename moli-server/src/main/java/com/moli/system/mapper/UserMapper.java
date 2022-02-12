package com.moli.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moli.common.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
