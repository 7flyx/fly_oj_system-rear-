package com.fly.system.domain.question.vo;

import com.fly.system.domain.question.dto.QuestionAddDTO;
import lombok.Data;

@Data
public class QuestionEditDTO extends QuestionAddDTO {
    private Long questionId;
}
