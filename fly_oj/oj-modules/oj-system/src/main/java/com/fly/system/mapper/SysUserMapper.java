package com.fly.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
