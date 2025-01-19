package com.fly.system.service;

import com.fly.common.core.domain.R;

public interface ISysUserService {
    R<Void> login(String userAccount , String password);
}
