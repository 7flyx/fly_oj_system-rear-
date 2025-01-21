package com.fly.common.core.constants;

// 和缓存相关的常量

import com.fly.common.core.enums.UserIdentity;

public class CacheConstants {
    public final static String LOGIN_TOKEN_KEY = "logintoken:";
    public final static Long EXP = 720L; // 过期时间
    public static final long REFRESH_TIME = 180L;
}
