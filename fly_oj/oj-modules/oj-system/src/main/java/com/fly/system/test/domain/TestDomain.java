package com.fly.system.test.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_test")
public class TestDomain {
    private String testId;
    private String title;
    private String content;
}
