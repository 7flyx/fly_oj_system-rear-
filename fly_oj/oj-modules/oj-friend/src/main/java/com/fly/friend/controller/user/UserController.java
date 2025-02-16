package com.fly.friend.controller.user;

import com.fly.common.core.constants.HttpConstants;
import com.fly.common.core.controller.BaseController;
import com.fly.common.core.domain.R;
import com.fly.common.core.domain.user.LoginUserVO;
import com.fly.friend.domain.user.dto.UserDTO;
import com.fly.friend.domain.user.dto.UserUpdateDTO;
import com.fly.friend.domain.user.vo.UserVO;
import com.fly.friend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/logout")
    public R<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return toR(userService.logout(token)); // true ok, false 不ok
    }

    @GetMapping("/info")
    public R<LoginUserVO> info(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return userService.info(token);
    }

    @GetMapping("/detail")
    public R<UserVO> detail() {
        return R.success(userService.detail());
    }

    @PutMapping("/edit")
    public R<Void> edit(@RequestBody UserUpdateDTO userUpdateDTO) {
        return toR(userService.edit(userUpdateDTO));
    }

    @PutMapping("/head-image/update")
    public R<Void> updateHeadImage(@RequestBody UserUpdateDTO userUpdateDTO) {
        return toR(userService.updateHeadImage(userUpdateDTO.getHeadImage()));
    }
}
