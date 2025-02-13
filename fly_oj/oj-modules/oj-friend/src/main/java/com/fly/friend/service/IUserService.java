package com.fly.friend.service;

import com.fly.friend.domain.user.dto.UserDTO;

public interface IUserService {
    boolean sendCode(UserDTO userDTO);

    String codeLogin(String phone, String code);
}
