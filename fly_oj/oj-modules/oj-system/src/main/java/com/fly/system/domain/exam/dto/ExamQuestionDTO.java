package com.fly.system.domain.exam.dto;

import lombok.Data;

import java.util.LinkedHashSet;

@Data
public class ExamQuestionDTO {
    private Long examId;
    private LinkedHashSet<Long> questionIdSet;
}
