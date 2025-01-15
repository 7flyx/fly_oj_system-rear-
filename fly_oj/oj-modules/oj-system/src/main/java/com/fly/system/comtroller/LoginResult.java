package com.fly.system.comtroller;

import lombok.Data;

@Data
public class LoginResult {
    private int code; // 0失败，1成功
    private String msg;
}
