package com.fly.friend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fly.common.core.constants.CacheConstants;
import com.fly.common.core.enums.ResultCode;
import com.fly.common.core.enums.UserIdentity;
import com.fly.common.message.service.AliSmsService;
import com.fly.common.redis.service.RedisService;
import com.fly.common.security.exception.ServiceException;
import com.fly.common.security.service.TokenService;
import com.fly.friend.domain.user.User;
import com.fly.friend.domain.user.dto.UserDTO;
import com.fly.friend.mapper.UserMapper;
import com.fly.friend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AliSmsService aliSmsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TokenService tokenService;

    @Value("${sms.code-expiration:5}")
    private Long phoneCodeExpiration; // 验证码有效时间，默认是5分钟

    @Value("${sms.send-limit:3}")
    private Long sendLimit;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public boolean sendCode(UserDTO userDTO) {
        if (!checkPhone(userDTO.getPhone())) {
            throw new ServiceException(ResultCode.FAILED_USER_PHONE);
        }
        Long restTime = redisService.getExpire(getPhoneCodeKey(userDTO.getPhone()), TimeUnit.SECONDS);
        if(restTime != null && phoneCodeExpiration * 60 - restTime <= 60) { // 发送验证码的间隔时间是1分钟，小于1分钟的时候就拦截
            throw new ServiceException(ResultCode.FAILED_FREQUENT);
        }
        // 每天一个电话号获取验证码的此时有一个限制，50次。第二题计数清零
        String codeTimeKey = getCodeTimeKey(userDTO.getPhone());
        Long sendTimes = redisService.getCacheObject(codeTimeKey, Long.class); // 当前电话号发送验证码的次数
        if (sendTimes != null && sendTimes >= sendLimit) { // 超过了50次，拦截
            throw new ServiceException(ResultCode.FAILED_TIME_LIMIT);
        }

        String code = RandomUtil.randomNumbers(6);
        // key = p:c:电话号
        redisService.setCacheObject(getPhoneCodeKey(userDTO.getPhone()), code,
                phoneCodeExpiration, TimeUnit.MINUTES); // 将验证码存储到redis中
        boolean sendMobileCode =  aliSmsService.sendMobileCode(userDTO.getPhone(), code); // 将验证码交给阿里云那边，进行发送短信
        if (!sendMobileCode) {
            throw new ServiceException(ResultCode.FAILED_SEND_CODE);
        }
        redisService.increment(codeTimeKey);

        if(sendTimes == null) { // 说明是当天第一次发起获取验证码的请求
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
            // 设置有效时间，知道第二题凌晨0点0分0秒。
            redisService.expire(codeTimeKey, seconds, TimeUnit.SECONDS);
        }
        return true;
    }


    @Override
    public String codeLogin(String phone, String code) {
        // 1、先判断验证码是都有效
        checkCode(phone, code);

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone));
        if (user == null) { // 新用户，先注册账号
            user = new User();
            user.setPhone(phone);
            userMapper.insert(user);
        }

        // 新建token，并返回
        String token = tokenService.createToken(user.getUserId(), secret,
                UserIdentity.ORDINARY.getValue(), user.getNickName());
        return token;
    }

    private void checkCode(String phone, String code) {
        String phoneCodeKey = getPhoneCodeKey(phone);
        String cacheCode = redisService.getCacheObject(phoneCodeKey, String.class);
        if (StrUtil.isEmpty(cacheCode)) { // 验证码过期
            throw new ServiceException(ResultCode.FAILED_INVALID_CODE);
        }
        if (!cacheCode.equals(code)) { // 验证码错误
            throw new ServiceException(ResultCode.FAILED_ERROR_CODE);
        }
        // 验证码匹配成功之后，就应该在redis中删除这个验证码，省空间
        redisService.deleteObject(phoneCodeKey);
    }

    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }

    private String getPhoneCodeKey(String phone) {
        return CacheConstants.PHONE_CODE_KEY + phone;
    }

    private String getCodeTimeKey(String phone) {
        return CacheConstants.CODE_TIMER_KEY + phone;
    }
}
