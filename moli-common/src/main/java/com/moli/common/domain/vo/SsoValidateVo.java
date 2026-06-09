package com.moli.common.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * SSO 校验结果：仅身份凭证，不含角色/菜单（子系统自行加载本地 RBAC）。
 */
@Data
public class SsoValidateVo {

    @ApiModelProperty("认证中心用户ID（子系统需映射为本地用户）")
    private Long userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("姓名")
    private String nickName;

    @ApiModelProperty("系统编码")
    private String systemCode;

    @ApiModelProperty("认证中心 Session Token；仅用于回调中心 API，不能代替子系统本地鉴权")
    private String hubToken;

}
