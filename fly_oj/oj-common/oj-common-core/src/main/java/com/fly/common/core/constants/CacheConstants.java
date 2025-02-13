package com.fly.common.core.constants;

// 和缓存相关的常量

import com.fly.common.core.enums.UserIdentity;

public class CacheConstants {
    public final static String LOGIN_TOKEN_KEY = "logintoken:"; // 管理员用户的key
    public final static Long EXP = 720L; // 登录用户的过期时间
    public static final long REFRESH_TIME = 180L; // 登录用户的一次延长时间

    public final static String PHONE_CODE_KEY = "p:c:"; // 手机验证码的key
    public final static String CODE_TIMER_KEY = "c:t:"; // 手机验证码一天发送验证码次数 的key

}

