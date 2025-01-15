package com.fly.common.core.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {
    private Long createBy; // 创建人
    private LocalDateTime createTime;
    private Long updateBy; // 更新人
    private LocalDateTime updateTime;
}
