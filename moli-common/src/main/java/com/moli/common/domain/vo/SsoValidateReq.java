package com.moli.common.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SsoValidateReq {

    @ApiModelProperty("一次性 Ticket")
    private String ticket;

    @ApiModelProperty("系统编码，须与 Ticket 一致")
    private String systemCode;

}
