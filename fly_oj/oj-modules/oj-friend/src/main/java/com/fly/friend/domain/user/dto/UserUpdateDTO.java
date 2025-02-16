package com.fly.friend.domain.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserUpdateDTO {
    private String headImage;
    private String nickName;
    private Integer sex;
    private String phone;
    private String email;
    private String wechat; // vx号
    private String schoolName; // 学校名
    private String majorName; // 专业名
    private String introduce; // 个人介绍
}
