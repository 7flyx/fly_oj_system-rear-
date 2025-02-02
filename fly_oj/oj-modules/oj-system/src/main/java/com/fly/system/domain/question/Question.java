package com.fly.system.domain.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fly.common.core.domain.BaseEntity;
import lombok.Data;

@TableName("tb_question")
@Data
public class Question extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID) // 雪花算法
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
