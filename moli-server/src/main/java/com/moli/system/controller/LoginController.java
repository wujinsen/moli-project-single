package com.moli.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moli.common.constant.CommonConstant;
import com.moli.common.core.MoliResult;
import com.moli.common.domain.entity.SysLoginLog;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.vo.CaptchaImageVo;
import com.moli.common.domain.vo.LoginVo;
import com.moli.common.domain.vo.MenuVo;
import com.moli.common.enums.ResponseCodeEnums;
import com.moli.common.exception.BaseException;
import com.moli.common.utils.IpUtils;
import com.moli.common.utils.ServletUtils;
import com.moli.common.utils.SpringUtil;
import com.moli.config.util.ShiroUtils;
import com.moli.system.mapper.SysLoginLogMapper;
import com.moli.system.mapper.SysUserMapper;
import com.moli.system.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("")
@Api(tags = "登录管理")
@Slf4j
public class LoginController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private SysUserMapper sysUserMapper;

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
            BeanUtils.copyProperties(user, request);
            request.setPassword("");
            request.setSalt("");
            loginVo.setUser(request);
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
        BeanUtils.copyProperties(ShiroUtils.getUserInfo(), request);
        request.setPassword("");
        request.setSalt("");
        //用户信息
        loginVo.setUser(request);
        List<MenuVo> menuVoList = menuService.selectMenuTreeByUserId(user.getId());
        //菜单信息
        loginVo.setMenuVoList(menuVoList);
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
        CaptchaImageVo captchaImageVo = new CaptchaImageVo();
        BufferedImage image = null;
//        String capText = captchaProducerMath.createText();
//        capStr = capText.substring(0, capText.lastIndexOf("@"));
//        code = capText.substring(capText.lastIndexOf("@") + 1);
//        image = captchaProducerMath.createImage(capStr);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (Exception e) {
            new BaseException("验证码异常");
        }

//        Base64.encode(os.toByteArray()));
//        captchaImageVo.setImg();
//        captchaImageVo.setUuid();
        return MoliResult.success(captchaImageVo);
    }

    public void insertLoginLog(SysUser sysUser, String msg, Integer status) {
            // 保存数据库
            SysLoginLog sysLoginLog = new SysLoginLog();
            if (sysUser != null && StringUtils.isNotBlank(sysUser.getUserName())) {
                sysLoginLog.setUserName(sysUser.getUserName());
            }
            String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
            sysLoginLog.setIpAddress(ip);
//            sysLoginLog.setBrowser(browser);
//            sysLoginLog.setLoginAddress(os);
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
