package com.fly.friend.test;

import com.fly.common.core.controller.BaseController;
import com.fly.common.core.domain.R;
import com.fly.common.message.service.AliSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
public class TestController extends BaseController {
    @Autowired
    private AliSmsService aliSmsService;

    @GetMapping("/sendCode")
    public R<Void> sendCode(String phone, String code) {
        return toR(aliSmsService.sendMobileCode(phone, code));
    }
}
