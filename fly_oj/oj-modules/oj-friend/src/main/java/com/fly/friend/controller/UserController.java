package com.fly.friend.controller;

import com.fly.common.core.controller.BaseController;
import com.fly.common.core.domain.R;
import com.fly.friend.domain.user.dto.UserDTO;
import com.fly.friend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @PostMapping("/sendCode")
    public R<Void> sendCode(@RequestBody UserDTO userDTO) {
        return toR(userService.sendCode(userDTO));
    }

    // /code/login
    @PostMapping("/code/login")
    public R<String> codeLogin(@RequestBody UserDTO userDTO) {
        return R.success(userService.codeLogin(userDTO.getPhone(), userDTO.getCode()));
    }
}
