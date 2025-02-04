package com.fly.system.controller.question;

import com.fly.common.core.controller.BaseController;
import com.fly.common.core.domain.R;
import com.fly.common.core.domain.TableDataInfo;
import com.fly.system.domain.question.dto.QuestionAddDTO;
import com.fly.system.domain.question.dto.QuestionQueryDTO;
import com.fly.system.domain.question.vo.QuestionDetailVO;
import com.fly.system.domain.question.vo.QuestionEditDTO;
import com.fly.system.service.question.IQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/question")
@Tag(name = "题目管理相关的接口")
public class QuestionController extends BaseController {

    @Autowired
    private IQuestionService questionService;

    // 题目列表
    @GetMapping("/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        return getTableDataInfo(questionService.list(questionQueryDTO));
    }

    // 添加题目
    @PostMapping("/add")
    public R<Void> add(@RequestBody QuestionAddDTO questionAddDTO) {
        return toR(questionService.add(questionAddDTO));
    }

    // 编辑页面 详情
    @GetMapping("/detail")
    public R<QuestionDetailVO> detail(Long questionId) {
        return R.success(questionService.detail(questionId));
    }

    // 编辑页面 修改
    @PutMapping("/edit")
    public R<Void> edit(@RequestBody QuestionEditDTO questionEditDTO) {
        return toR(questionService.edit(questionEditDTO));
    }

    @DeleteMapping("/delete")
    public R<Void> delete(Long questionId) {
        return toR(questionService.delete(questionId));
    }
}
