package com.fly.system.domain.question.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionAddDTO {
    @NotNull
    private String title;
    @Min(value = 1, message = "最小值1")
    @Max(value = 3, message = "最大值3")
    private Integer difficulty;
    private Long timeLimit;
    private Long spaceLimit;
    @NotNull
    private String content;
    private String questionCase;
    private String defaultCode;
    private String mainFunc;
}
