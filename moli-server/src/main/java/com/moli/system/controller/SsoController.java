package com.moli.system.controller;

import com.moli.common.core.MoliResult;
import com.moli.common.domain.vo.SsoValidateReq;
import com.moli.common.domain.vo.SsoValidateVo;
import com.moli.system.service.SsoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sso")
@Api(tags = "单点登录 Ticket")
public class SsoController {

    @Autowired
    private SsoService ssoService;

    @Value("${sso.shared-secret:}")
    private String sharedSecret;

    @PostMapping("/validate")
    @ApiOperation(value = "校验 SSO Ticket", notes = "子系统服务端调用；请求头可带 X-Sso-Secret")
    public MoliResult<SsoValidateVo> validate(@RequestBody SsoValidateReq req,
                                              @RequestHeader(value = "X-Sso-Secret", required = false) String headerSecret) {
        if (StringUtils.isNotBlank(sharedSecret)) {
            String provided = StringUtils.isNotBlank(headerSecret) ? headerSecret : "";
            if (!sharedSecret.equals(provided)) {
                return MoliResult.errorMsg(com.moli.common.enums.ResponseCodeEnums.AUTHOR_ERROR_CODE.getCode(), "SSO 密钥无效");
            }
        }
        return MoliResult.success(ssoService.validateTicket(req.getTicket(), req.getSystemCode()));
    }

}
