package com.fly.system.domain.question.dto;

import com.fly.common.core.domain.PageQueryDTO;
import lombok.Data;

@Data
public class QuestionQueryDTO extends PageQueryDTO {
    private Integer difficulty;
    private String title;
}
