package com.fly.friend.service.user;

import com.fly.common.core.domain.R;
import com.fly.common.core.domain.user.LoginUserVO;
import com.fly.friend.domain.user.dto.UserDTO;
import com.fly.friend.domain.user.dto.UserUpdateDTO;
import com.fly.friend.domain.user.vo.UserVO;

public interface IUserService {
    boolean sendCode(UserDTO userDTO);

    String codeLogin(String phone, String code);

    boolean logout(String token);

    R<LoginUserVO> info(String token);

    UserVO detail();

    int edit(UserUpdateDTO userUpdateDTO);

    int updateHeadImage(String headImage);
}
