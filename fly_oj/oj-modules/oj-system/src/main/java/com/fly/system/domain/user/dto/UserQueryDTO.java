package com.fly.system.domain.user.dto;

import com.fly.common.core.domain.PageQueryDTO;
import com.github.pagehelper.PageInfo;
import lombok.Data;

@Data
public class UserQueryDTO extends PageQueryDTO {
    private Long userId;
    private String nickName;
}
