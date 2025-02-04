package com.fly.system.domain.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fly.common.core.domain.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 竞赛
 */
@Data
@TableName("tb_exam")
public class Exam extends BaseEntity {
    @TableId(value = "EXAM_ID", type = IdType.ASSIGN_ID) // 雪花算法
    private Long examId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status; // 是否发布竞赛
}
