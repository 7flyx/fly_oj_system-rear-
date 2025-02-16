package com.fly.friend.domain.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserVO {
    @JsonSerialize(using = ToStringSerializer.class) // 数据传输时，加工成字符串；接收前端的时间时，自动转换成long
    private Long userId; // 主键id
    private String nickName;
    private String headImage; // 头像的地址
    private Integer sex;
    private String phone;
    private String email;
    private String wechat; // vx号
    private String schoolName; // 学校名
    private String majorName; // 专业名
    private String introduce; // 个人介绍
}
