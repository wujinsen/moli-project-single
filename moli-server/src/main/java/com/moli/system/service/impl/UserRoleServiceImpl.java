package com.moli.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moli.common.domain.entity.SysUserRole;
import com.moli.system.mapper.UserRoleMapper;
import com.moli.system.service.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, SysUserRole> implements UserRoleService {

}
