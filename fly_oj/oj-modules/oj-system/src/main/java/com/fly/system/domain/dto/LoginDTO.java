package com.fly.system.domain.dto;

import lombok.Data;

@Data
// DTO (data transfer object) 数据传输对象，即前端传输过来的数据对象
public class LoginDTO {
    private String userAccount;
    private String password;
}
