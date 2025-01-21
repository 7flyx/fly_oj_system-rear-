package com.fly.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
// VO (view object, 视图对象)，用于在展示层显示数据
public class SysUserVO {
    @Schema(description = "用户账号")
    private String userAccount;
    @Schema(description = "昵称")
    private String nickName;
}
