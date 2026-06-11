package com.moli.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.moli.common.constant.CommonConstant;
import com.moli.common.constant.RedisConstant;
import com.moli.common.constant.SystemConstant;
import com.moli.common.domain.dto.SsoTicketPayload;
import com.moli.common.domain.entity.SysSystem;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.vo.SsoValidateVo;
import com.moli.common.exception.BaseException;
import com.moli.config.util.RedisUtil;
import com.moli.system.service.SsoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SsoServiceImpl implements SsoService {

    @Autowired
    private RedisUtil redisUtil;

    @Value("${sso.ticket-ttl-seconds:60}")
    private int ticketTtlSeconds;

    @Value("${sso.entry-path:/sso/login}")
    private String defaultEntryPath;

    @Override
    public String createTicket(SysUser user, SysSystem system, String hubToken) {
        SsoTicketPayload payload = new SsoTicketPayload();
        payload.setUserId(user.getId());
        payload.setUserName(user.getUserName());
        payload.setNickName(user.getNickName());
        payload.setSystemId(system.getId());
        payload.setSystemCode(system.getSystemCode());
        payload.setHubToken(hubToken);
        payload.setFullPermission(CommonConstant.hasFullPermission(user.getUserName()));

        String ticketId = UUID.randomUUID().toString().replace("-", "");
        String key = String.format(RedisConstant.SSO_TICKET_KEY, ticketId);
        int ttl = ticketTtlSeconds > 0 ? ticketTtlSeconds : RedisConstant.SSO_TICKET_EXPIRE;
        redisUtil.set(key, JSON.toJSONString(payload), ttl);
        return ticketId;
    }

    @Override
    public SsoValidateVo validateTicket(String ticket, String systemCode) {
        SsoTicketPayload payload = parseTicket(ticket);
        if (payload == null) {
            throw new BaseException("Ticket 无效或已过期");
        }
        if (StringUtils.isBlank(systemCode) || !systemCode.equals(payload.getSystemCode())) {
            throw new BaseException("系统编码不匹配");
        }
        redisUtil.del(String.format(RedisConstant.SSO_TICKET_KEY, ticket));

        SsoValidateVo vo = new SsoValidateVo();
        vo.setUserId(payload.getUserId());
        vo.setUserName(payload.getUserName());
        vo.setNickName(payload.getNickName());
        vo.setSystemCode(payload.getSystemCode());
        vo.setHubToken(payload.getHubToken());
        vo.setFullPermission(payload.getFullPermission());
        return vo;
    }

    @Override
    public String buildRedirectUrl(SysSystem system, String ticket) {
        String baseUrl = trimTrailingSlash(system.getBaseUrl());
        if (StringUtils.isBlank(baseUrl)) {
            throw new BaseException("系统未配置访问地址");
        }
        String entryPath = StringUtils.isNotBlank(system.getEntryPath()) ? system.getEntryPath() : defaultEntryPath;
        if (!entryPath.startsWith("/")) {
            entryPath = "/" + entryPath;
        }
        return baseUrl + entryPath + "?ticket=" + ticket + "&systemCode=" + system.getSystemCode();
    }

    @Override
    public SsoTicketPayload parseTicket(String ticket) {
        if (StringUtils.isBlank(ticket)) {
            return null;
        }
        Object raw = redisUtil.get(String.format(RedisConstant.SSO_TICKET_KEY, ticket));
        if (raw == null) {
            return null;
        }
        return JSON.parseObject(raw.toString(), SsoTicketPayload.class);
    }

    private String trimTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        String trimmed = url.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    public boolean isExternal(SysSystem system) {
        return SystemConstant.SSO_MODE_EXTERNAL.equalsIgnoreCase(system.getSsoMode());
    }

}
