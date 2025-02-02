package com.fly.system.domain.question.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 *  题目编辑详情时
 */
@Data
public class QuestionDetailVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long questionId;
    private String title;
    private Integer difficulty;
    private Long timeLimit;
    private Long spaceLimit;
    private String content;
    private String questionCase;
    private String defaultCode;
    private String mainFunc;
}
