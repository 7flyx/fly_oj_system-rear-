package com.fly.system.service.sysuser.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fly.common.core.constants.HttpConstants;
import com.fly.common.core.domain.LoginUser;
import com.fly.common.core.domain.R;
import com.fly.common.core.enums.ResultCode;
import com.fly.common.core.enums.UserIdentity;
import com.fly.common.security.exception.ServiceException;
import com.fly.common.security.service.TokenService;
import com.fly.system.domain.sysuser.SysUser;
import com.fly.system.domain.sysuser.dto.SysUserSaveDTO;
import com.fly.system.domain.sysuser.vo.LoginUserVO;
import com.fly.system.mapper.sysyser.SysUserMapper;
import com.fly.system.service.sysuser.ISysUserService;
import com.fly.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RefreshScope // 不需要重启服务，配置文件更新后就可以生效
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private TokenService tokenService; // 内部会生成token，并且存入redis中去

//    @Value("${jwt.secret}") // 来自于配置文件，随时可以更换
//    private String secret;

    @Value("${jwt.secret}")
    private String secret;
    @Override
    public R<String> login(String userAccount, String password) {
        // 去数据库中查询信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        // select password from tb_sys_user where user_acount = #{userAccount}
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper
                .select(SysUser::getUserId, SysUser::getPassword, SysUser::getNickName).eq(SysUser::getUserAccount, userAccount));
        if (sysUser == null) {
            return R.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        // 将传输过来的密码跟数据库的密码进行对比
        if (BCryptUtils.matchesPassword(password, sysUser.getPassword())) {
            // 调用TokenServices的代码，会生成Token，并且写入redis中
            String token = tokenService.createToken(sysUser.getUserId(), secret, UserIdentity.ADMIN.getValue(), sysUser.getNickName());
            return R.success(token);
        }
        return R.failed(ResultCode.FAILED_LOGIN);
    }

    @Override
    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.deleteLoginUser(token, secret);
    }


    @Override
    public R<LoginUserVO> info(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        LoginUser loginUser = tokenService.getLoginUser(token, secret);
        if (loginUser == null) {
            R.failed();
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setNickName(loginUser.getNickName());
        return R.success(loginUserVO);
    }

    @Override
    public int add(SysUserSaveDTO sysUserSaveDTO) {
        // 1、将DTO转换为实体 SysUser

        List<SysUser> sysUserList = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserAccount, sysUserSaveDTO.getUserAccount()));

        if (CollectionUtil.isNotEmpty(sysUserList)) {
            // 用户已存在
            // 加 自定义异常
            throw new ServiceException(ResultCode.FAILED_USER_EXISTS);
        }

        SysUser sysUser = new SysUser();
        sysUser.setUserAccount(sysUserSaveDTO.getUserAccount());
        sysUser.setPassword(sysUserSaveDTO.getPassword());

        sysUser.setCreateBy(100L); // 创建人，获取当前用户的id
        sysUser.setCreateTime(LocalDateTime.now());

        int row = sysUserMapper.insert(sysUser);
        return row;
    }

}
