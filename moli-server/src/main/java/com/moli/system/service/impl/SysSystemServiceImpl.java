package com.moli.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moli.common.constant.CommonConstant;
import com.moli.common.constant.SystemConstant;
import com.moli.common.constant.SystemGroupConstant;
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
import com.moli.common.domain.vo.CapabilitiesVo;
import com.moli.system.service.MenuService;
import com.moli.system.service.PermissionService;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    @Autowired
    private PermissionService permissionService;

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
        if (CommonConstant.hasFullPermission(userName)) {
            return sysSystemMapper.selectById(systemId) != null;
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
            result.setPermissions(Collections.emptyList());
            result.setFullPermission(false);
            return result;
        }

        result.setMenuVoList(resolveMenus(user));
        applyCapabilities(result, user);
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
        if (StringUtils.isNotBlank(query.getSystemGroup())) {
            wrapper.eq(SysSystem::getSystemGroup, query.getSystemGroup());
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
        if (StringUtils.isBlank(system.getSystemName())) {
            throw new BaseException("系统名称不能为空");
        }
        Integer exists = sysSystemMapper.selectCount(new LambdaQueryWrapper<SysSystem>()
                .eq(SysSystem::getSystemCode, system.getSystemCode()));
        if (exists != null && exists > 0) {
            throw new BaseException("系统编码已存在");
        }
        if (system.getStatus() == null) {
            system.setStatus(SystemConstant.STATUS_ENABLED);
        }
        normalizeAndValidate(system, null);
        sysSystemMapper.insert(system);
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateSystem(SysSystem system) {
        if (system.getId() == null) {
            throw new BaseException("系统 ID 不能为空");
        }
        SysSystem existing = sysSystemMapper.selectById(system.getId());
        if (existing == null) {
            throw new BaseException("系统不存在");
        }
        if (StringUtils.isNotBlank(system.getSystemCode())
                && !system.getSystemCode().equals(existing.getSystemCode())) {
            throw new BaseException("系统编码不可修改");
        }
        system.setSystemCode(existing.getSystemCode());
        if (StringUtils.isBlank(system.getSystemName())) {
            system.setSystemName(existing.getSystemName());
        }
        normalizeAndValidate(system, existing);
        sysSystemMapper.updateById(system);
        return Boolean.TRUE;
    }

    @Override
    public Boolean deleteSystems(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Boolean.TRUE;
        }
        for (Long id : ids) {
            SysSystem system = sysSystemMapper.selectById(id);
            if (system != null) {
                assertDeletable(system);
            }
        }
        sysSystemMapper.deleteBatchIds(ids);
        sysUserSystemMapper.delete(new LambdaQueryWrapper<SysUserSystem>().in(SysUserSystem::getSystemId, ids));
        return Boolean.TRUE;
    }

    private void normalizeAndValidate(SysSystem system, SysSystem existing) {
        if (StringUtils.isBlank(system.getSsoMode())) {
            system.setSsoMode(existing == null ? SystemConstant.SSO_MODE_INTERNAL : existing.getSsoMode());
        }
        if (StringUtils.isBlank(system.getSystemGroup())) {
            system.setSystemGroup(existing == null ? SystemGroupConstant.DEFAULT : existing.getSystemGroup());
        }
        system.setSystemGroup(SystemGroupConstant.normalize(system.getSystemGroup()));
        if (isExternal(system)) {
            if (StringUtils.isBlank(system.getBaseUrl())) {
                throw new BaseException("外部系统必须填写访问地址 baseUrl");
            }
            if (StringUtils.isBlank(system.getEntryPath())) {
                system.setEntryPath("/sso/login");
            }
        }
    }

    private void assertDeletable(SysSystem system) {
        if (SystemConstant.DEFAULT_SYSTEM_CODE.equals(system.getSystemCode())) {
            throw new BaseException("默认系统 moli-admin 不可删除");
        }
    }

    @Override
    public List<Long> listSystemIdsByUserId(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user != null && CommonConstant.hasFullPermission(user.getUserName())) {
            return listAllSystemIds();
        }
        List<SysUserSystem> relations = sysUserSystemMapper.selectList(new LambdaQueryWrapper<SysUserSystem>()
                .eq(SysUserSystem::getUserId, userId));
        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }
        return relations.stream().map(SysUserSystem::getSystemId).collect(Collectors.toList());
    }

    @Override
    public List<Long> listUserIdsBySystemId(Long systemId) {
        if (systemId == null) {
            return Collections.emptyList();
        }
        Set<Long> userIds = new LinkedHashSet<>();
        List<SysUserSystem> relations = sysUserSystemMapper.selectList(new LambdaQueryWrapper<SysUserSystem>()
                .eq(SysUserSystem::getSystemId, systemId));
        if (CollectionUtils.isNotEmpty(relations)) {
            relations.stream().map(SysUserSystem::getUserId).forEach(userIds::add);
        }
        List<SysUser> fullPermissionUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .in(SysUser::getUserName, CommonConstant.SUPER_ADMIN, CommonConstant.LEGACY_SUPER_ADMIN)
                .eq(SysUser::getIsDelete, CommonConstant.UN_DELETE));
        if (CollectionUtils.isNotEmpty(fullPermissionUsers)) {
            fullPermissionUsers.stream().map(SysUser::getId).forEach(userIds::add);
        }
        return new ArrayList<>(userIds);
    }

    private List<Long> listAllSystemIds() {
        List<SysSystem> systems = sysSystemMapper.selectList(new LambdaQueryWrapper<SysSystem>()
                .orderByAsc(SysSystem::getSort)
                .orderByAsc(SysSystem::getId));
        if (CollectionUtils.isEmpty(systems)) {
            return Collections.emptyList();
        }
        return systems.stream().map(SysSystem::getId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUserSystems(Long userId, List<Long> systemIds) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user != null && CommonConstant.hasFullPermission(user.getUserName())) {
            return;
        }
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
                .orderByAsc(SysSystem::getSort)
                .orderByAsc(SysSystem::getId);
        if (CommonConstant.hasFullPermission(userName)) {
            return sysSystemMapper.selectList(wrapper);
        }
        wrapper.eq(SysSystem::getStatus, SystemConstant.STATUS_ENABLED);
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
        if (CommonConstant.hasFullPermission(user.getUserName())) {
            return menuService.getMenuTreeAll();
        }
        return menuService.selectMenuTreeByUserId(user.getId());
    }

    private boolean isExternal(SysSystem system) {
        return SystemConstant.SSO_MODE_EXTERNAL.equalsIgnoreCase(system.getSsoMode());
    }

    private void applyCapabilities(SystemEnterVo result, SysUser user) {
        CapabilitiesVo capabilities = permissionService.buildCapabilities(user.getId(), user.getUserName());
        result.setPermissions(capabilities.getPermissions());
        result.setFullPermission(capabilities.getFullPermission());
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
