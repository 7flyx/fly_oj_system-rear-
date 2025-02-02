package com.fly.common.security.service;

import cn.hutool.core.lang.UUID;
import com.fly.common.core.constants.CacheConstants;
import com.fly.common.core.constants.JwtConstants;
import com.fly.common.redis.service.RedisService;
import com.fly.common.core.domain.LoginUser;
import com.fly.common.core.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// 操作用户 登录Token的方法
@Service
@Slf4j
public class TokenService {

    @Autowired
    private RedisService redisService;

    public String createToken(Long userId, String secret, Integer identity, String nickName) {
        Map<String, Object> claims = new HashMap<>(); // 唯一标识,也就是token中的数据载体部分
        String userKey = UUID.fastUUID().toString(); // tutool的jar包
        claims.put(JwtConstants.LOGIN_USER_ID, userId);
        claims.put(JwtConstants.LOGIN_USER_KEY, userKey);
        String token = JwtUtils.createToken(claims, secret); // 这个secret类似于盐，用于Token进行 数字签名
        // 在redis中存储敏感信息
        String key = getTokenKey(userKey); // redis中的key值。加上了前缀 logintoken
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(identity); // 1 普通用户；2 管理员用户
        loginUser.setNickName(nickName);
        // 写入redis中，并且规定 过期时间 720分钟
        redisService.setCacheObject(key, loginUser, CacheConstants.EXP, TimeUnit.MINUTES);
        return token;
    }

    // 延迟token的有效时间，就是延长redis中存储的用于用户身份认证的敏感信息的有效时间 439544273
    public void extendToken(String token, String secret){
//        Claims claims;
//        try {
//            claims = JwtUtils.parseToken(token, secret);
//            if (claims == null) {
//                log.error("解析token {},出现异常, ",token);
//                return;
//            }
//        } catch (Exception e) {
//            log.error("解析token {},出现异常, ",token, e);
//            return;
//        }

        String userKey = getUserKey(token, secret);
        String tokenKey = getTokenKey(userKey);

        Long expire = redisService.getExpire(tokenKey, TimeUnit.MINUTES);
        if (expire != null && expire < CacheConstants.REFRESH_TIME) {
            redisService.expire(tokenKey, CacheConstants.EXP, TimeUnit.MINUTES);
        }

    }

    private String getTokenKey(String userKey) {
        return CacheConstants.LOGIN_TOKEN_KEY + userKey;
    }

    private String getUserKey(String token, String secret) {
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret);
            if (claims == null) {
                log.error("解析token {},出现异常, ",token);
                return null;
            }
        } catch (Exception e) {
            log.error("解析token {},出现异常, ",token, e);
            return null;
        }

       return JwtUtils.getUserKey(claims); // 获取jwt中的key
    }

    public LoginUser getLoginUser(String token, String secret) {
        String userKey = getUserKey(token, secret);
        if(userKey == null) return null;
        return redisService.getCacheObject(getTokenKey(userKey), LoginUser.class);
    }

    public boolean deleteLoginUser(String token, String secret) {
        String userKey = getUserKey(token, secret);
        if(userKey == null) return false;
        return redisService.deleteObject(getTokenKey(userKey));
    }
}
