package com.fly.system.domain.question.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *  查询题目列表时
 */
@Data
public class QuestionVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long questionId; // 雪花算法生成的数值太大，前端在接收会，会产生截断的，这里就在传输的时候，序列化成 字符串就行
    private String title;
    private Integer difficulty;

    private String createName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 将日期格式化成 常用的形式，就是转换成了字符串的形式 进行传输
    private LocalDateTime createTime;
}
