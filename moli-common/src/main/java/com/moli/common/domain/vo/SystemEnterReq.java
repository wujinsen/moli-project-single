package com.moli.common.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemEnterReq {

    @ApiModelProperty("要进入的系统ID")
    private Long systemId;

}
