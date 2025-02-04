package com.fly.system.domain.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamAddDTO {
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 他会自动将前端传输的字符串类型，转换为 LocalDateTime
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 他会自动将前端传输的字符串类型，转换为 LocalDateTime
    private LocalDateTime endTime;
}
