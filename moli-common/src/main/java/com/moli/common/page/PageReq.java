package com.moli.common.page;


import com.moli.common.core.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageReq<T> extends BasePage {
    @ApiModelProperty("请求的分页条件")
    private T data;

    public PageReq(){
        this.pageNum = 1;
        this.pageSize = 10;
    }
}
