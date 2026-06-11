package com.moli.system.service;

import com.moli.common.domain.entity.SysSystem;
import com.moli.common.domain.vo.SystemEnterVo;
import com.moli.common.domain.vo.SystemVo;
import com.moli.common.page.PageRes;

import java.util.List;

public interface SysSystemService {

    boolean isPortalEnabled();

    List<SystemVo> listByUserId(Long userId, String userName);

    boolean userCanAccess(Long userId, String userName, Long systemId);

    SystemEnterVo enterSystem(Long userId, String userName, Long systemId);

    PageRes<SysSystem> page(SysSystem query);

    Boolean saveSystem(SysSystem system);

    Boolean updateSystem(SysSystem system);

    Boolean deleteSystems(List<Long> ids);

    List<Long> listSystemIdsByUserId(Long userId);

    List<Long> listUserIdsBySystemId(Long systemId);

    void assignUserSystems(Long userId, List<Long> systemIds);

    SystemVo getCurrentSystemVo();

}
