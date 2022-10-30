package com.moli.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moli.common.domain.entity.SysUserRole;
import com.moli.system.mapper.SysUserRoleMapper;
import com.moli.system.service.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements UserRoleService {

}
