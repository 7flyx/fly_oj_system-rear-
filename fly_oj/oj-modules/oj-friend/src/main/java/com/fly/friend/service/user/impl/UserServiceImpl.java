package com.fly.friend.service.user.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fly.common.core.constants.CacheConstants;
import com.fly.common.core.constants.Constants;
import com.fly.common.core.constants.HttpConstants;
import com.fly.common.core.domain.LoginUser;
import com.fly.common.core.domain.R;
import com.fly.common.core.domain.user.LoginUserVO;
import com.fly.common.core.enums.ResultCode;
import com.fly.common.core.enums.UserIdentity;
import com.fly.common.core.enums.UserStatus;
import com.fly.common.message.service.AliSmsService;
import com.fly.common.redis.service.RedisService;
import com.fly.common.security.exception.ServiceException;
import com.fly.common.security.service.TokenService;
import com.fly.friend.domain.user.User;
import com.fly.friend.domain.user.dto.UserDTO;
import com.fly.friend.domain.user.dto.UserUpdateDTO;
import com.fly.friend.domain.user.vo.UserVO;
import com.fly.common.core.utils.ThreadLocalUtil;
import com.fly.friend.mapper.user.UserMapper;
import com.fly.friend.manager.UserCacheManager;
import com.fly.friend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    private UserCacheManager userCacheManager;

    @Value("${sms.code-expiration:5}")
    private Long phoneCodeExpiration; // 验证码有效时间，默认是5分钟

    @Value("${sms.send-limit:3}")
    private Long sendLimit;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${sms.is-send:false}")
    private boolean isSend; // 区分开发环境和生产环境。true：生产环境；false：开发环境

    @Value("${file.oss.downloadUrl}")
    private String downloadUrl;

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
        // 开发环境，默认验证码就是123456
        String code = isSend? RandomUtil.randomNumbers(6) : Constants.DEFAULT_CODE;
        // key = p:c:电话号
        redisService.setCacheObject(getPhoneCodeKey(userDTO.getPhone()), code,
                phoneCodeExpiration, TimeUnit.MINUTES); // 将验证码存储到redis中
        if (isSend) { // 区分开发环境和生产环境
            boolean sendMobileCode =  aliSmsService.sendMobileCode(userDTO.getPhone(), code); // 将验证码交给阿里云那边，进行发送短信
            if (!sendMobileCode) {
                throw new ServiceException(ResultCode.FAILED_SEND_CODE);
            }
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
            user.setStatus(UserStatus.NORMAL.getValue());
            userMapper.insert(user);
        }

        // 新建token，并返回
        String token = tokenService.createToken(user.getUserId(), secret,
                UserIdentity.ORDINARY.getValue(), user.getNickName(), user.getHeadImage());
        return token;
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
//        loginUserVO.setHeadImage(downloadUrl + loginUserVO.getHeadImage());
        if (StrUtil.isNotEmpty(loginUser.getHeadImage())) {
            loginUserVO.setHeadImage(downloadUrl + loginUser.getHeadImage());
        }
        return R.success(loginUserVO);
    }

    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }

    @Override
    public UserVO detail() {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        UserVO userVO = userCacheManager.getUserById(userId);
        if (userVO == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if (StrUtil.isNotEmpty(userVO.getHeadImage())) {
            userVO.setHeadImage(downloadUrl + userVO.getHeadImage());
        }
        return userVO;
    }

    @Override
    public int edit(UserUpdateDTO userUpdateDTO) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        user.setNickName(userUpdateDTO.getNickName());
        user.setSex(userUpdateDTO.getSex());
        user.setSchoolName(userUpdateDTO.getSchoolName());
        user.setMajorName(userUpdateDTO.getMajorName());
        user.setPhone(userUpdateDTO.getPhone());
        user.setEmail(userUpdateDTO.getEmail());
        user.setWechat(userUpdateDTO.getWechat());
        user.setIntroduce(userUpdateDTO.getIntroduce());
        //更新用户缓存
        userCacheManager.refreshUser(user);
        tokenService.refreshLoginUser(user.getNickName(),user.getHeadImage(),
                ThreadLocalUtil.get(Constants.USER_KEY, String.class));
        return userMapper.updateById(user);
    }

    @Override
    public int updateHeadImage(String headImage) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        user.setHeadImage(headImage);
        //更新用户缓存
        userCacheManager.refreshUser(user);
        tokenService.refreshLoginUser(user.getNickName(),user.getHeadImage(),
                ThreadLocalUtil.get(Constants.USER_KEY, String.class));
        userMapper.updateById(user);
        return userMapper.updateById(user);
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

    private String getPhoneCodeKey(String phone) {
        return CacheConstants.PHONE_CODE_KEY + phone;
    }

    private String getCodeTimeKey(String phone) {
        return CacheConstants.CODE_TIMER_KEY + phone;
    }
}
