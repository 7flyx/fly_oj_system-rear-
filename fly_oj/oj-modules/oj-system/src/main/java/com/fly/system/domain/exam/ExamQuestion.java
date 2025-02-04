package com.fly.system.domain.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fly.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 竞赛题目
 */
@Data
@TableName("tb_exam_question")
public class ExamQuestion extends BaseEntity {
    @TableId(value = "EXAM_QUESTION_ID", type = IdType.ASSIGN_ID)
    private Long examQuestionId;
    private Long examId;
    private Long questionId;
    private Integer questionOrder;
}
