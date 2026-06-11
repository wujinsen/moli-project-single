package com.moli.common.domain.dto;

import lombok.Data;

@Data
public class SsoTicketPayload {

    private Long userId;

    private String userName;

    private String nickName;

    private Long systemId;

    private String systemCode;

    private String hubToken;

    /** 超管标记：外部系统可按此授予本地最大权限 */
    private Boolean fullPermission;

}
