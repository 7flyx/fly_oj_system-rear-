package com.fly.system.service.sysuser;

import com.fly.common.core.domain.R;
import com.fly.system.domain.sysuser.dto.SysUserSaveDTO;
import com.fly.system.domain.sysuser.vo.LoginUserVO;

public interface ISysUserService {
    R<String> login(String userAccount , String password);

    boolean logout(String token);

    // 返回值是数据库执行后，影响的行数
    int add(SysUserSaveDTO sysUserSaveDTO);

    R<LoginUserVO> info(String token);
}
