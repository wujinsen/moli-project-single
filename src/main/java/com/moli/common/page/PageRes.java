package com.moli.common.page;


import com.moli.common.core.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PageRes<T> extends BasePage {

    @ApiModelProperty("总条数")
    private Integer total;

    @ApiModelProperty("返回数据")
    private List<T> items;

    public void setData(List<T> data, Integer pageNum, Integer pageSize) {
        this.items = data;
        if (null == pageNum || null == pageSize) {
            pageNum = 1;
            pageSize = 10;
        }
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public void setData(List<T> data) {
        this.items = data;
        this.pageNum = 1;
        this.pageSize = 10;
    }
}
