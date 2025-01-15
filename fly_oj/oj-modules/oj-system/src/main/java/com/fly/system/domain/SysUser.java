package com.fly.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fly.common.core.domain.BaseEntity;
import lombok.Data;

@Data
@TableName("tb_sys_user") // 与数据库中的表相关联
public class SysUser extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID) // mybatis-plus 提供了雪花算法，生成64位的唯一性ID。【1bit不用，41位时间戳，10位机器表示，12位序列号】
    private Long userId; // 主键，不再使用自增主键
    private String userAccount; // 账号
    private String password; // 密码
}
