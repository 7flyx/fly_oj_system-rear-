package com.fly.common.core.domain;

import lombok.Data;

@Data
public class PageQueryDTO {
    private Integer pageSize = 10; //默认值 分页查询 每一页的条数
    private Integer pageNum = 1; //默认值 分页查询 第几页
}
