package com.moli.system.service;

import com.moli.common.domain.dto.SsoTicketPayload;
import com.moli.common.domain.entity.SysSystem;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.domain.vo.SsoValidateVo;

public interface SsoService {

    String createTicket(SysUser user, SysSystem system, String hubToken);

    SsoValidateVo validateTicket(String ticket, String systemCode);

    String buildRedirectUrl(SysSystem system, String ticket);

    SsoTicketPayload parseTicket(String ticket);

}
