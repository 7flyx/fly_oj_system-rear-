package com.fly.friend.controller.question;

import com.fly.common.core.controller.BaseController;
import com.fly.common.core.domain.R;
import com.fly.common.core.domain.TableDataInfo;
import com.fly.friend.domain.question.QuestionQueryDTO;
import com.fly.friend.domain.question.vo.QuestionDetailVO;
import com.fly.friend.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class QuestionController extends BaseController {

    @Autowired
    private IQuestionService questionService;

    @GetMapping("/semiLogin/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        return questionService.list(questionQueryDTO);
    }

    // 根据题目id，拿到题目的全部数据，在答题时 需要
    @GetMapping("/detail")
    public R<QuestionDetailVO> detail(Long questionId) {
        return R.success(questionService.detail(questionId));
    }

    @GetMapping("/preQuestion")
    public R<String> preQuestion(Long questionId) {
       return R.success(questionService.preQuestion(questionId));
    }

    @GetMapping("/nextQuestion")
    public R<String> nextQuestion(Long questionId) {
        return R.success(questionService.nextQuestion(questionId));
    }
}
