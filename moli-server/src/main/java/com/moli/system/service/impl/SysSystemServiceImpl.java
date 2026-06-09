package com.moli.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.constant.SystemConstant;
import com.moli.common.domain.entity.SysSystem;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.entity.SysUserSystem;
import com.moli.common.domain.vo.MenuVo;
import com.moli.common.domain.vo.SystemEnterVo;
import com.moli.common.domain.vo.SystemVo;
import com.moli.common.exception.BaseException;
import com.moli.common.page.PageRes;
import com.moli.config.util.ShiroUtils;
import com.moli.system.mapper.SysSystemMapper;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.mapper.SysUserSystemMapper;
import com.moli.system.service.MenuService;
import com.moli.system.service.SsoService;
import com.moli.system.service.SysSystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysSystemServiceImpl implements SysSystemService {

    @Autowired
    private SysSystemMapper sysSystemMapper;

    @Autowired
    private SysUserSystemMapper sysUserSystemMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private MenuService menuService;

    @Autowired
    private SsoService ssoService;

    @Value("${sso.enabled:true}")
    private boolean ssoEnabled;

    @Override
    public boolean isPortalEnabled() {
        if (!ssoEnabled) {
            return false;
        }
        try {
            Integer count = sysSystemMapper.selectCount(new LambdaQueryWrapper<SysSystem>()
                    .eq(SysSystem::getStatus, SystemConstant.STATUS_ENABLED));
            return count != null && count > 0;
        } catch (Exception e) {
            log.warn("sys_system 表不可用，多系统门户已降级为关闭: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<SystemVo> listByUserId(Long userId, String userName) {
        if (!isPortalEnabled()) {
            return Collections.emptyList();
        }
        List<SysSystem> systems = listAccessibleSystems(userId, userName);
        if (CollectionUtils.isEmpty(systems)) {
            return Collections.emptyList();
        }
        Map<Long, Integer> defaultMap = loadDefaultMap(userId);
        return systems.stream().map(system -> toSystemVo(system, defaultMap.get(system.getId()))).collect(Collectors.toList());
    }

    @Override
    public boolean userCanAccess(Long userId, String userName, Long systemId) {
        if (!isPortalEnabled() || systemId == null) {
            return false;
        }
        if (CommonConstant.isSuperAdmin(userName)) {
            SysSystem system = sysSystemMapper.selectById(systemId);
            return system != null && SystemConstant.STATUS_ENABLED.equals(system.getStatus());
        }
        Integer count = sysUserSystemMapper.selectCount(new LambdaQueryWrapper<SysUserSystem>()
                .eq(SysUserSystem::getUserId, userId)
                .eq(SysUserSystem::getSystemId, systemId));
        if (count == null || count == 0) {
            return false;
        }
        SysSystem system = sysSystemMapper.selectById(systemId);
        return system != null && SystemConstant.STATUS_ENABLED.equals(system.getStatus());
    }

    @Override
    public SystemEnterVo enterSystem(Long userId, String userName, Long systemId) {
        if (!userCanAccess(userId, userName, systemId)) {
            throw new BaseException("无权限进入该系统");
        }
        SysSystem system = sysSystemMapper.selectById(systemId);
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BaseException("用户不存在");
        }

        ShiroUtils.setCurrentSystem(system.getId(), system.getSystemCode());

        SystemEnterVo result = new SystemEnterVo();
        result.setCurrentSystem(toSystemVo(system,
                isDefaultSystem(userId, systemId) ? CommonConstant.YES : CommonConstant.NO));
        result.setHubToken(ShiroUtils.getSession().getId().toString());

        if (isExternal(system)) {
            String ticket = ssoService.createTicket(user, system, result.getHubToken());
            result.setRedirectUrl(ssoService.buildRedirectUrl(system, ticket));
            result.setMenuVoList(Collections.emptyList());
            return result;
        }

        result.setMenuVoList(resolveMenus(user));
        return result;
    }

    @Override
    public PageRes<SysSystem> page(SysSystem query) {
        PageRes<SysSystem> result = new PageRes<>();
        Page<SysSystem> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysSystem> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(query.getSystemName())) {
            wrapper.like(SysSystem::getSystemName, query.getSystemName());
        }
        if (StringUtils.isNotBlank(query.getSystemCode())) {
            wrapper.like(SysSystem::getSystemCode, query.getSystemCode());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysSystem::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(SysSystem::getSort).orderByDesc(SysSystem::getCreateTime);
        sysSystemMapper.selectPage(page, wrapper);
        result.setList(page.getRecords());
        result.setPageNum(query.getPageNum());
        result.setPageSize(query.getPageSize());
        result.setTotal((int) page.getTotal());
        return result;
    }

    @Override
    public Boolean saveSystem(SysSystem system) {
        if (StringUtils.isBlank(system.getSystemCode())) {
            throw new BaseException("系统编码不能为空");
        }
        Integer exists = sysSystemMapper.selectCount(new LambdaQueryWrapper<SysSystem>()
                .eq(SysSystem::getSystemCode, system.getSystemCode()));
        if (exists != null && exists > 0) {
            throw new BaseException("系统编码已存在");
        }
        if (system.getStatus() == null) {
            system.setStatus(SystemConstant.STATUS_ENABLED);
        }
        if (StringUtils.isBlank(system.getSsoMode())) {
            system.setSsoMode(SystemConstant.SSO_MODE_INTERNAL);
        }
        sysSystemMapper.insert(system);
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateSystem(SysSystem system) {
        sysSystemMapper.updateById(system);
        return Boolean.TRUE;
    }

    @Override
    public Boolean deleteSystems(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Boolean.TRUE;
        }
        sysSystemMapper.deleteBatchIds(ids);
        sysUserSystemMapper.delete(new LambdaQueryWrapper<SysUserSystem>().in(SysUserSystem::getSystemId, ids));
        return Boolean.TRUE;
    }

    @Override
    public List<Long> listSystemIdsByUserId(Long userId) {
        List<SysUserSystem> relations = sysUserSystemMapper.selectList(new LambdaQueryWrapper<SysUserSystem>()
                .eq(SysUserSystem::getUserId, userId));
        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }
        return relations.stream().map(SysUserSystem::getSystemId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUserSystems(Long userId, List<Long> systemIds) {
        sysUserSystemMapper.delete(new LambdaQueryWrapper<SysUserSystem>().eq(SysUserSystem::getUserId, userId));
        if (CollectionUtils.isEmpty(systemIds)) {
            return;
        }
        boolean defaultAssigned = false;
        for (Long systemId : systemIds) {
            SysUserSystem relation = new SysUserSystem();
            relation.setUserId(userId);
            relation.setSystemId(systemId);
            if (!defaultAssigned) {
                relation.setIsDefault(CommonConstant.YES);
                defaultAssigned = true;
            } else {
                relation.setIsDefault(CommonConstant.NO);
            }
            sysUserSystemMapper.insert(relation);
        }
    }

    private List<SysSystem> listAccessibleSystems(Long userId, String userName) {
        LambdaQueryWrapper<SysSystem> wrapper = new LambdaQueryWrapper<SysSystem>()
                .eq(SysSystem::getStatus, SystemConstant.STATUS_ENABLED)
                .orderByAsc(SysSystem::getSort)
                .orderByAsc(SysSystem::getId);
        if (CommonConstant.isSuperAdmin(userName)) {
            return sysSystemMapper.selectList(wrapper);
        }
        List<Long> systemIds = listSystemIdsByUserId(userId);
        if (CollectionUtils.isEmpty(systemIds)) {
            return Collections.emptyList();
        }
        wrapper.in(SysSystem::getId, systemIds);
        return sysSystemMapper.selectList(wrapper);
    }

    private Map<Long, Integer> loadDefaultMap(Long userId) {
        List<SysUserSystem> relations = sysUserSystemMapper.selectList(new LambdaQueryWrapper<SysUserSystem>()
                .eq(SysUserSystem::getUserId, userId));
        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyMap();
        }
        return relations.stream().collect(Collectors.toMap(SysUserSystem::getSystemId,
                r -> r.getIsDefault() == null ? CommonConstant.NO : r.getIsDefault(), (a, b) -> a));
    }

    private boolean isDefaultSystem(Long userId, Long systemId) {
        SysUserSystem relation = sysUserSystemMapper.selectOne(new LambdaQueryWrapper<SysUserSystem>()
                .eq(SysUserSystem::getUserId, userId)
                .eq(SysUserSystem::getSystemId, systemId)
                .last("limit 1"));
        return relation != null && Objects.equals(relation.getIsDefault(), CommonConstant.YES);
    }

    private SystemVo toSystemVo(SysSystem system, Integer isDefaultFlag) {
        SystemVo vo = new SystemVo();
        BeanUtils.copyProperties(system, vo);
        vo.setIsDefault(Objects.equals(isDefaultFlag, CommonConstant.YES));
        return vo;
    }

    private List<MenuVo> resolveMenus(SysUser user) {
        if (CommonConstant.isSuperAdmin(user.getUserName())) {
            return menuService.getMenuTreeAll();
        }
        return menuService.selectMenuTreeByUserId(user.getId());
    }

    private boolean isExternal(SysSystem system) {
        return SystemConstant.SSO_MODE_EXTERNAL.equalsIgnoreCase(system.getSsoMode());
    }

    @Override
    public SystemVo getCurrentSystemVo() {
        Long systemId = ShiroUtils.getCurrentSystemId();
        if (systemId == null) {
            return null;
        }
        SysSystem system = sysSystemMapper.selectById(systemId);
        if (system == null) {
            return null;
        }
        SysUser user = ShiroUtils.getUserInfo();
        Long userId = user == null ? null : user.getId();
        return toSystemVo(system, userId == null ? CommonConstant.NO : (isDefaultSystem(userId, systemId) ? CommonConstant.YES : CommonConstant.NO));
    }

}
