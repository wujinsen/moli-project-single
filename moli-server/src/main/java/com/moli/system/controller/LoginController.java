package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moli.common.constant.CommonConstant;
import com.moli.common.constant.Constants;
import com.moli.common.constant.SystemConstant;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysLoginLog;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.vo.CaptchaImageVo;
import com.moli.common.domain.vo.LoginVo;
import com.moli.common.domain.vo.MenuVo;
import com.moli.common.domain.vo.SystemEnterVo;
import com.moli.common.domain.vo.SystemVo;
import com.moli.common.enums.ResponseCodeEnums;
import com.moli.common.exception.BaseException;
import com.moli.common.utils.IpUtils;
import com.moli.common.utils.ServletUtils;
import com.moli.common.utils.UserAgentUtils;
import com.moli.common.utils.SpringUtil;
import com.moli.config.util.RedisUtil;
import com.moli.config.util.ShiroUtils;
import com.moli.system.mapper.SysLoginLogMapper;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.service.MenuService;
import com.moli.system.service.SysSystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("")
@Api(tags = "登录管理")
@Slf4j
public class LoginController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private SysSystemService sysSystemService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${captcha.enabled:false}")
    private boolean captchaEnabled;

    /**
     * 登录方法
     *
     * @param request 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public MoliResult login(@RequestBody SysUser request) {
        String userName = request.getUserName();
        LoginVo loginVo = new LoginVo();

        MoliResult<LoginVo> result = new MoliResult<>();
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserName, userName).eq(SysUser::getIsDelete, CommonConstant.UN_DELETE));
        if (null == user) {
            result.setMsg("用户不存在或者密码错误");
            result.setCode(ResponseCodeEnums.ERROR.getCode());
            insertLoginLog(user, result.getMsg(), 0);
            return result;
        }
        Subject subject = SecurityUtils.getSubject();

        try {
            // 将用户请求参数封装后，直接提交给Shiro处理
            UsernamePasswordToken token = new UsernamePasswordToken(userName, request.getPassword());
            // 登录认证
            subject.login(token);
        } catch (IncorrectCredentialsException e) {
            result.setMsg("用户不存在或者密码错误");
            result.setCode(ResponseCodeEnums.ERROR.getCode());
            log.error("login IncorrectCredentialsException: {}", e.getMessage());
            user.setPassword("");
            user.setSalt("");
            loginVo.setUser(user);
            result.setData(loginVo);
            insertLoginLog(ShiroUtils.getUserInfo(), result.getMsg(), 0);
            return result;
        } catch (LockedAccountException e) {
            result.setMsg("登录失败，该用户已被冻结");
            result.setCode(ResponseCodeEnums.ERROR.getCode());
            log.error("login LockedAccountException: {}", e.getMessage());
            insertLoginLog(ShiroUtils.getUserInfo(), result.getMsg(), 0);
            return result;
        } catch (AuthenticationException e) {
            result.setMsg("用户认证失败");
            result.setCode(ResponseCodeEnums.ERROR.getCode());
            log.error("login AuthenticationException: {} ", e.getMessage());
            insertLoginLog(ShiroUtils.getUserInfo(), result.getMsg(), 0);
            return result;
        } catch (Exception e) {
            result.setMsg("未知异常");
            result.setCode(ResponseCodeEnums.ERROR.getCode());
            log.error("login Exception: {} ", e.getMessage());
            insertLoginLog(ShiroUtils.getUserInfo(), result.getMsg(), 0);
            return result;
        }
        //token
        loginVo.setToken(ShiroUtils.getSession().getId().toString());
        ShiroUtils.bindUserSession(user.getUserName());
        user.setPassword("");
        user.setSalt("");
        //用户信息
        loginVo.setUser(user);
        fillLoginContext(loginVo, user);
        result.setMsg("登录成功");
        result.setCode(ResponseCodeEnums.SUCCESS_CODE.getCode());
        result.setData(loginVo);
        insertLoginLog(ShiroUtils.getUserInfo(), result.getMsg(), 1);
        return result;
    }

    /**
     * 退出
     */
    @PostMapping("/logout")
    @ApiOperation(value = "退出系统")
    public MoliResult logout() {
        //  ShiroUtils.deleteCache(ShiroUtils.getUserInfo().getTelephone(), true);
        ShiroUtils.logout();
        return MoliResult.success();
    }

    /**
     * 验证码
     */
    @PostMapping("/captchaImage")
    @ApiOperation(value = "验证码")
    public MoliResult<CaptchaImageVo> captchaImage() {
        if (!captchaEnabled) {
            return MoliResult.errorMsg(ResponseCodeEnums.SERVICE_ERROR_CODE.getCode(), "验证码功能暂时关闭");
        }
        CaptchaImageVo captchaImageVo = new CaptchaImageVo();
        String code = generateCaptchaCode(4);
        String uuid = UUID.randomUUID().toString();
        BufferedImage image = createCaptchaImage(code);
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
            String key = Constants.CAPTCHA_CODE_KEY + uuid;
            redisUtil.set(key, code, Constants.CAPTCHA_EXPIRATION * 60L);
            captchaImageVo.setUuid(uuid);
            captchaImageVo.setImg(java.util.Base64.getEncoder().encodeToString(os.toByteArray()));
        } catch (IOException e) {
            log.error("captchaImage IOException: {}", e.getMessage(), e);
            throw new BaseException("验证码异常");
        }
        return MoliResult.success(captchaImageVo);
    }

    private String generateCaptchaCode(int length) {
        final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        }
        return sb.toString();
    }

    private BufferedImage createCaptchaImage(String code) {
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(new Font("Arial", Font.BOLD, 26));
            g.setColor(new Color(40, 40, 40));
            g.drawString(code, 22, 30);
        } finally {
            g.dispose();
        }
        return image;
    }

    private void fillLoginContext(LoginVo loginVo, SysUser user) {
        boolean portalEnabled = sysSystemService.isPortalEnabled();
        loginVo.setSystemPortalEnabled(portalEnabled);
        if (!portalEnabled) {
            loginVo.setMenuVoList(resolveMenus(user));
            return;
        }
        List<SystemVo> systemList = sysSystemService.listByUserId(user.getId(), user.getUserName());
        loginVo.setSystemList(systemList);
        if (CollectionUtils.isEmpty(systemList)) {
            loginVo.setMenuVoList(new ArrayList<>());
            return;
        }
        if (systemList.size() == 1 && isInternalSystem(systemList.get(0))) {
            SystemEnterVo enter = sysSystemService.enterSystem(user.getId(), user.getUserName(), systemList.get(0).getId());
            loginVo.setCurrentSystem(enter.getCurrentSystem());
            loginVo.setMenuVoList(enter.getMenuVoList());
            return;
        }
        loginVo.setMenuVoList(new ArrayList<>());
    }

    private List<MenuVo> resolveMenus(SysUser user) {
        if (CommonConstant.isSuperAdmin(user.getUserName())) {
            return menuService.getMenuTreeAll();
        }
        return menuService.selectMenuTreeByUserId(user.getId());
    }

    private boolean isInternalSystem(SystemVo system) {
        return !SystemConstant.SSO_MODE_EXTERNAL.equalsIgnoreCase(system.getSsoMode());
    }

    public void insertLoginLog(SysUser sysUser, String msg, Integer status) {
            // 保存数据库
            SysLoginLog sysLoginLog = new SysLoginLog();
            if (sysUser != null && StringUtils.isNotBlank(sysUser.getUserName())) {
                sysLoginLog.setUserName(sysUser.getUserName());
            }
            HttpServletRequest request = ServletUtils.getRequest();
            String ip = IpUtils.getIpAddr(request);
            sysLoginLog.setIpAddress(ip);
            sysLoginLog.setLoginAddress(IpUtils.getLoginLocation(ip));
            sysLoginLog.setBrowser(UserAgentUtils.getBrowser(request));
            sysLoginLog.setOs(UserAgentUtils.getOs(request));
            sysLoginLog.setStatus(status);
            sysLoginLog.setRemark(msg);
            sysLoginLog.setLoginTime(new Date());
        try {
            SpringUtil.getBean(SysLoginLogMapper.class).insert(sysLoginLog);
        } catch (Exception e) {
            log.error("SysLoginLogMapper insert error: {}", e.getMessage());
        }

    }
}
