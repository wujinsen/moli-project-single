package com.moli.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SysOperationLog {

    private Long id;
    @ApiModelProperty("创建时间")

    private String title;
    @ApiModelProperty("业务类型（ 1新增 2修改 3删除 4导入 5导出 6其它）")
    private Integer businessType;
    @ApiModelProperty("请求方法名")
    private String methodName;
    @ApiModelProperty("请求方式")
    private String requestMethod;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("ip地址")
    private String requestIp;
    @ApiModelProperty("url")
    private String requestUrl;
    @ApiModelProperty("请求参数")
    private String requestParam;
    @ApiModelProperty("返回参数")
    private String responseResult;

    @ApiModelProperty("操作状态（1正常 0异常）")
    private Integer status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;

}

