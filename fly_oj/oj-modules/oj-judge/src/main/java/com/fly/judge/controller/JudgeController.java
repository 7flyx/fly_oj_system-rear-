package com.fly.judge.controller;

import com.fly.api.domain.dto.JudgeSubmitDTO;
import com.fly.api.domain.vo.UserQuestionResultVO;
import com.fly.common.core.controller.BaseController;
import com.fly.common.core.domain.R;
import com.fly.judge.service.IJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/judge")
public class JudgeController extends BaseController {

    @Autowired
    private IJudgeService judgeService;

    @PostMapping("/doJudgeJavaCode")
    public R<UserQuestionResultVO> doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO) {
        return R.success(judgeService.doJudgeJavaCode(judgeSubmitDTO));
    }

}
