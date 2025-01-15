package com.fly.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fly.system.comtroller.LoginResult;
import com.fly.system.domain.SysUser;
import com.fly.system.mapper.SysUserMapper;
import com.fly.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Override
    public LoginResult login(String userAccount, String password) {
        // 去数据库中查询信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        // select password from tb_sys_user where user_acount = #{userAccount}
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper
                .select(SysUser::getPassword).eq(SysUser::getUserAccount, userAccount));
        LoginResult result = new LoginResult();
        if (sysUser == null) {
            result.setCode(0);
            result.setMsg("当前登录用户不存在");
            return result;
        }
        if (sysUser.getPassword().equals(password)) {
            result.setCode(1);
            return result;
        }
        result.setCode(0);
        result.setMsg("账号或密码错误");
        return result;
    }
}
