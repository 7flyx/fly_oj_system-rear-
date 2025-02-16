package com.fly.friend.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.friend.domain.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
