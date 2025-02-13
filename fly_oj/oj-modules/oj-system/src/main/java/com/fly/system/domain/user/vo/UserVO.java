package com.fly.system.domain.user.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long userId;
    private String nickName;
    private String headImage; // 头像的地址
    private Integer sex;
    private String phone;
    private String email;
    private String wechat; // vx号
    private String schoolName; // 学校名
    private String majorName; // 专业名
    private String introduce; // 个人介绍
    private Integer status;  // 状态，比如 正常、拉黑
}
