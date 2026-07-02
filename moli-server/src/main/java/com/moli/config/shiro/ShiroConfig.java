package com.moli.config.shiro;

import com.moli.config.util.SHA256Util;
import com.moli.system.service.PermissionService;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置类
 */
@Configuration
public class ShiroConfig {

    private final String CACHE_KEY = "shiro:cache:";
    private final String SESSION_KEY = "shiro:session:";

    //Redis配置
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.timeout:3000}")
    private int redisTimeout;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.database}")
    private int database;

    @Value("${shiro.session-expire-seconds:86400}")
    private int sessionExpireSeconds;

    /**
     * 开启Shiro-aop注解支持
     * @Attention 使用代理方式所以需要开启代码支持
     * @Author Sans
     * @CreateTime 2019/6/12 8:38
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * Shiro基础配置
     * @Author Sans
     * @CreateTime 2019/6/12 8:42
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactory(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 注意过滤器配置顺序不能颠倒
        // 配置过滤:不会被拦截的链接
        //Swagger接口
        filterChainDefinitionMap.put("/doc.html", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/v2/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/sso/validate", "anon");
        filterChainDefinitionMap.put("/**", "authc");
        //未授权界面
        //配置shiro默认登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
        shiroFilterFactoryBean.getFilters().put("authc", new AuthenticationFilter());
        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了，登出后跳转配置的loginUrl
        shiroFilterFactoryBean.getFilters().put("logout", new SystemLogoutFilter());
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    /**
     * 安全管理器
     * @Author Sans
     * @CreateTime 2019/6/12 10:34
     */
    @Bean
    public SecurityManager securityManager(ShiroRealm shiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 自定义Ssession管理
        securityManager.setSessionManager(sessionManager());
        // 自定义Cache实现
        securityManager.setCacheManager(cacheManager());
        // 自定义Realm验证
        securityManager.setRealm(shiroRealm);
        return securityManager;
    }

    /**
     * 身份验证器
     * @Author Sans
     * @CreateTime 2019/6/12 10:37
     */
    @Bean
    public ShiroRealm shiroRealm(PermissionService permissionService) {
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        shiroRealm.setPermissionService(permissionService);
        return shiroRealm;
    }

    /**
     * 凭证匹配器 将密码校验交给Shiro的SimpleAuthenticationInfo进行处理,在这里做匹配配置
     * @Author Sans
     * @CreateTime 2019/6/12 10:48
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher shaCredentialsMatcher = new HashedCredentialsMatcher();
        // 散列算法:这里使用SHA256算法;
        shaCredentialsMatcher.setHashAlgorithmName(SHA256Util.HASH_ALGORITHM_NAME);
        // 散列的次数，比如散列两次，相当于 md5(md5(""));
        shaCredentialsMatcher.setHashIterations(SHA256Util.HASH_ITERATIONS);
        return shaCredentialsMatcher;
    }

    /**
     * 配置Redis管理器
     * @Attention 使用的是shiro-redis开源插件
     * @Author Sans
     * @CreateTime 2019/6/12 11:06
     */
    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setTimeout(redisTimeout);
        redisManager.setPassword(password);
        redisManager.setDatabase(database);
        return redisManager;
    }

    /**
     * 配置Cache管理器 用于往Redis存储权限和角色标识
     * @Attention 使用的是shiro-redis开源插件
     * @Author Sans
     * @CreateTime 2019/6/12 12:37
     */
    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        redisCacheManager.setKeyPrefix(CACHE_KEY);
        // 配置缓存的话要求放在session里面的实体类必须有个id标识
        redisCacheManager.setPrincipalIdFieldName("userName");
        return redisCacheManager;
    }

    /**
     * 配置RedisSessionDAO
     * @Attention 使用的是shiro-redis开源插件
     * @Author Sans
     * @CreateTime 2019/6/12 13:44
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        redisSessionDAO.setKeyPrefix(SESSION_KEY);
        redisSessionDAO.setExpire(sessionExpireSeconds);
        return redisSessionDAO;
    }

    /**
     * SessionID生成器
     * @Author Sans
     * @CreateTime 2019/6/12 13:12
     */
    @Bean
    public ShiroSessionIdGenerator sessionIdGenerator() {
        return new ShiroSessionIdGenerator();
    }



    /**
     * 配置Session管理器
     * @Author Sans
     * @CreateTime 2019/6/12 14:25
     */
    @Bean
    public SessionManager sessionManager() {
        ShiroSessionManager shiroSessionManager = new ShiroSessionManager();
        shiroSessionManager.setSessionDAO(redisSessionDAO());
        shiroSessionManager.setGlobalSessionTimeout(sessionExpireSeconds * 1000L);
        return shiroSessionManager;
    }


}
