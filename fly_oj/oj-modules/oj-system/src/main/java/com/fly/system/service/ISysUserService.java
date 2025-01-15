package com.fly.system.service;

import com.fly.system.comtroller.LoginResult;

public interface ISysUserService {
    LoginResult login(String userAccount ,String password);
}
