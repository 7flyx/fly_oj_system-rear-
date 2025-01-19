package com.fly.system.test;

import com.fly.system.test.service.ITestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private ITestService testService;

    @GetMapping("/list")
    public List<?> list() {
        return testService.list();
    }

    @GetMapping("/log")
    public String log() {
        log.info("hello world");
        log.error("我是error级别的");
        return "操作成功";
    }

}
