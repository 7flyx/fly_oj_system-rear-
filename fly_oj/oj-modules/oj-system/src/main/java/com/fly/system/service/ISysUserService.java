package com.fly.system.service;

import com.fly.common.core.domain.R;
import com.fly.system.domain.dto.SysUserSaveDTO;

public interface ISysUserService {
    R<String> login(String userAccount , String password);

    // 返回值是数据库执行后，影响的行数
    int add(SysUserSaveDTO sysUserSaveDTO);
}
