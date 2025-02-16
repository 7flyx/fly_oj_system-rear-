package com.fly.friend.domain.question;

import com.fly.common.core.domain.PageQueryDTO;
import lombok.Data;

@Data
public class QuestionQueryDTO extends PageQueryDTO {
    private String keyword;
    private Integer difficulty;
}
