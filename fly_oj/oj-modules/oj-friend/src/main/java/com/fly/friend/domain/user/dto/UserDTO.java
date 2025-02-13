package com.fly.friend.domain.user.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String phone;
    private String code; // 验证码
}
