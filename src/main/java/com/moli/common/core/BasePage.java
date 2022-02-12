package com.moli.common.core;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasePage {
    @ApiModelProperty("当前页")
    protected Integer pageNum;

    @ApiModelProperty("每页数量")
    protected Integer pageSize;
}
