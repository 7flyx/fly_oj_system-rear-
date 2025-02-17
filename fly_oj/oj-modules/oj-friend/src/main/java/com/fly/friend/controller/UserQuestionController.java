package com.fly.friend.controller;

import com.fly.api.domain.vo.UserQuestionResultVO;
import com.fly.common.core.controller.BaseController;
import com.fly.common.core.domain.R;
import com.fly.friend.domain.user.dto.UserSubmitDTO;
import com.fly.friend.service.user.IUserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/question")
public class UserQuestionController extends BaseController {
    @Autowired
    private IUserQuestionService userQuestionService;

    @PostMapping("/submit")
    public R<UserQuestionResultVO> submit(@RequestBody UserSubmitDTO userSubmitDTO) {
        return userQuestionService.submit(userSubmitDTO);
    }
}
