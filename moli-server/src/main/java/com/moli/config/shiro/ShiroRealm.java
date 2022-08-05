package com.moli.config.shiro;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moli.common.constant.CommonConstant;
import com.moli.common.domain.entity.SysUser;
import com.moli.config.util.ShiroUtils;
import com.moli.system.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

/**
 * 自定义Realm, 实现Shiro安全认证
 */

@Slf4j
public class ShiroRealm extends AuthorizingRealm {

//    @Resource
//    private UserMapper userMapper;
//    @Resource
//    private UserRoleMapper userRoleMapper;
//
//    @Resource
//    private RoleMenuMapper roleMenuMapper;
//
//    @Resource
//    private RoleMapper roleMapper;
//
//    @Resource
//    private MenuMapper menuMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 身份认证
     *
     * @Author Sans
     * @CreateTime 2019/6/12 12:36
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获取用户的输入的账号.
        String userName = (String) authenticationToken.getPrincipal();
        //通过username从数据库中查找 User对象，如果找到进行验证
        //实际项目中,这里可以根据实际情况做缓存,如果不做,Shiro自己也是有时间间隔机制,2分钟内不会重复执行该方法
        SysUser user = userMapper.selectOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserName, userName).eq(SysUser::getIsDelete, CommonConstant.UN_DELETE));
//        //判断账号是否存在
//        if (user == null) {
//            throw new AuthenticationException();
//        }
//        //判断账号是否被冻结
//        if (user.getIsLock() == null || user.getIsLock() == 1) {
//            throw new LockedAccountException();
//        }

        //进行验证
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user,                                  //用户名
                user.getPassword(),                    //密码
                ByteSource.Util.bytes(user.getSalt()), //设置盐值
                getName()
        );
        //清除缓存和Session
        ShiroUtils.deleteCache(userName, true);
        return authenticationInfo;
    }

    /**
     * 授权权限
     * 用户进行权限验证时候Shiro会去缓存中找,如果查不到数据,会执行这个方法去查权限,并放入缓存中
     *
     * @Author Sans
     * @CreateTime 2019/6/12 11:44
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取用户ID
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
//        HtgUser htgUser = (HtgUser) principalCollection.getPrimaryPrincipal();
//        String userId = htgUser.getId();
//        //角色
//        Set<String> rolesSet = new HashSet<>();
//        //权限
//        Set<String> permsSet = new HashSet<>();
//
//        List<HtgUserRole> userRoleList = userRoleMapper.selectList(new QueryWrapper<HtgUserRole>().lambda().eq(HtgUserRole::getUserId, userId));
//        List<String> roleIdList = userRoleList.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
//        if (CollectionUtils.isNotEmpty(roleIdList)) {
//            List<HtgRole> roleList = roleMapper.selectList(new QueryWrapper<HtgRole>().lambda().in(HtgRole::getId, roleIdList));
//            for (HtgRole htgRole : roleList) {
//                //添加角色
//                rolesSet.add(htgRole.getRoleName());
//            }
//
//            List<HtgRoleMenu> roleMenuList = roleMenuMapper.selectList(new QueryWrapper<HtgRoleMenu>().lambda().in(HtgRoleMenu::getRoleId, roleIdList));
//            List<String> menuIdList = roleMenuList.stream().map(e -> e.getMenuId()).collect(Collectors.toList());
//            List<HtgMenu> menuList = new ArrayList<>();
//            if (CollectionUtils.isNotEmpty(menuIdList)) {
//                menuList = menuMapper.selectList(new QueryWrapper<HtgMenu>().lambda().in(HtgMenu::getId, menuIdList).eq(HtgMenu::getMenuType, MenuTypeEnum.BUTTON.getCode()));
//
//            }
//            permsSet.addAll(menuList.stream().map(e -> e.getPerms()).collect(Collectors.toSet()));
//            //将查到的权限和角色分别传入authorizationInfo中
//            authorizationInfo.setStringPermissions(permsSet);
//            authorizationInfo.setRoles(rolesSet);
//        }
        return authorizationInfo;
    }


}
