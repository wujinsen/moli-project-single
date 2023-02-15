package com.moli.system.service;

import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.vo.UserVo;
import com.moli.common.page.PageRes;

public interface UserService{

    PageRes<SysUser> list(UserVo userVo);

}
