package com.fly.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fly.common.core.domain.R;
import com.fly.common.core.enums.ResultCode;
import com.fly.system.domain.SysUser;
import com.fly.system.mapper.SysUserMapper;
import com.fly.system.service.ISysUserService;
import com.fly.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Override
    public R<Void> login(String userAccount, String password) {
        // 去数据库中查询信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        // select password from tb_sys_user where user_acount = #{userAccount}
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper
                .select(SysUser::getPassword).eq(SysUser::getUserAccount, userAccount));
        if (sysUser == null) {
            return R.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        // 将传输过来的密码跟数据库的密码进行对比
        if (BCryptUtils.matchesPassword(password, sysUser.getPassword())) {
            return R.success();
        }
        return R.failed(ResultCode.FAILED_LOGIN);
    }
}
