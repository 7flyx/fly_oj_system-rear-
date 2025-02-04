package com.fly.system.domain.exam.dto;

import com.fly.common.core.domain.PageQueryDTO;
import lombok.Data;

import java.time.LocalDateTime;

// 竞赛列表 查询DTO
// /exam/list
@Data
public class ExamQueryDTO extends PageQueryDTO {
    private String title;
    private String startTime;
    private String endTime;
}
