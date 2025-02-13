package com.fly.friend.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fly.common.core.domain.BaseEntity;
import lombok.Data;

@Data
@TableName("tb_user")
public class User extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class) // 数据传输时，加工成字符串；接收前端的时间时，自动转换成long
    @TableId(value = "USER_ID", type = IdType.ASSIGN_ID)
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
    private Integer status;  // 状态，比如 正常、拉黑
}
