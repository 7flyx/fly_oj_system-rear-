package com.fly.common.security.service;

import cn.hutool.core.lang.UUID;
import com.fly.common.core.constants.CacheConstants;
import com.fly.common.core.constants.JwtConstants;
import com.fly.common.redis.service.RedisService;
import com.fly.common.core.domain.LoginUser;
import com.fly.common.core.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// 操作用户 登录Token的方法
@Service
public class TokenService {

    @Autowired
    private RedisService redisService;
    public String createToken(Long userId, String secret, Integer identity) {
        Map<String, Object> claims = new HashMap<>(); // 唯一标识,也就是token中的数据载体部分
        String userKey = UUID.fastUUID().toString(); // tutool的jar包
        claims.put(JwtConstants.LOGIN_USER_ID, userId);
        claims.put(JwtConstants.LOGIN_USER_KEY, userKey);
        String token = JwtUtils.createToken(claims, secret); // 这个secret类似于盐，用于Token进行 数字签名
        // 在redis中存储敏感信息
        String key = CacheConstants.LOGIN_TOKEN_KEY + userKey; // redis中的key值。加上了前缀 logintoken
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(identity); // 1 普通用户；2 管理员用户
        // 写入redis中，并且规定 过期时间 720分钟
        redisService.setCacheObject(key, loginUser, CacheConstants.EXP, TimeUnit.MINUTES);
        return token;
    }
}
