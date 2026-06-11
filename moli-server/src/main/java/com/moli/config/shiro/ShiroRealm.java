package com.moli.config.shiro;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moli.common.constant.CommonConstant;
import com.moli.common.domain.entity.SysUser;
import com.moli.config.util.ShiroUtils;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.service.PermissionService;
import com.moli.common.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 自定义Realm, 实现Shiro安全认证
 */

@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Resource
    private SysUserMapper sysUserMapper;

    private PermissionService permissionService;

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 身份认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String userName = (String) authenticationToken.getPrincipal();
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda()
                .eq(SysUser::getUserName, userName)
                .eq(SysUser::getIsDelete, CommonConstant.UN_DELETE));
        if (user == null) {
            throw new UnknownAccountException();
        }
        // 与 sys_normal_disable 一致：1正常 0停用（停用不可登录）
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new LockedAccountException();
        }

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user,
                user.getPassword(),
                ByteSource.Util.bytes(user.getSalt()),
                getName()
        );
        if (isSingleSession()) {
            ShiroUtils.deleteCache(userName, true);
        }
        return authenticationInfo;
    }

    private boolean isSingleSession() {
        try {
            Environment env = SpringUtil.getBean(Environment.class);
            return Boolean.TRUE.equals(env.getProperty("shiro.single-session", Boolean.class, false));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 授权：从角色-菜单加载 perms，与 @RequiresPermissions 一致
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        SysUser user = (SysUser) principalCollection.getPrimaryPrincipal();
        if (user == null || permissionService == null) {
            return authorizationInfo;
        }
        Set<String> permissions = permissionService.getPermissionsByUserId(user.getId(), user.getUserName());
        authorizationInfo.setStringPermissions(permissions);
        return authorizationInfo;
    }
}
