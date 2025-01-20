package com.fly.common.security.utils;
import com.fly.common.core.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.HashMap;
import java.util.Map;
public class JwtUtils {
    /**
     * ⽣成令牌
     *
     * @param claims 数据
     * @param secret 密钥
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims, String secret) {
        String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }
    /**
     * 从令牌中获取数据
     *
     * @param token 令牌
     * @param secret 密钥
     * @return 数据
     */
    public static Claims parseToken(String token, String secret) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public static String getUserKey(Claims claims) {
        Object value = claims.get(JwtConstants.LOGIN_USER_KEY); // 看上文存储的key值
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    public static String getUserId(Claims claims) {
        Object value = claims.get(JwtConstants.LOGIN_USER_ID);
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    public static void main(String[] args) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", 123456789L);
//        System.out.println(createToken(claims, "ashfjkhfkjdshfjhfdf"));

        String token = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjEyMzQ1Njc4OX0.zMU0LwLkM9zK0aSJt4FGqMtQnT5EOO0D5hBI1mina4EBuFHw9nRi9cIWqi_nPqcZGN5UAtwTvKcTz-b-O6yp5g";
        Claims claims = parseToken(token, "ashfjkhfkjdshfjhfdf");
        System.out.println(claims);
    }
}