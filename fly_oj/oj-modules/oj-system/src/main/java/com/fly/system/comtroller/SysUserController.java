package com.fly.system.comtroller;

import com.fly.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysUserController {
    @Autowired
    private ISysUserService sysUserService;
    public LoginResult login(String userAccount, String password) {
        return sysUserService.login(userAccount, password);
    }
}
