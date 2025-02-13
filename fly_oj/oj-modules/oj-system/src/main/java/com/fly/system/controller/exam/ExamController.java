package com.fly.system.controller.exam;

import com.fly.common.core.controller.BaseController;
import com.fly.common.core.domain.R;
import com.fly.common.core.domain.TableDataInfo;
import com.fly.system.domain.exam.dto.ExamAddDTO;
import com.fly.system.domain.exam.dto.ExamEditDTO;
import com.fly.system.domain.exam.dto.ExamQueryDTO;
import com.fly.system.domain.exam.dto.ExamQuestionDTO;
import com.fly.system.domain.exam.vo.ExamDetailVO;
import com.fly.system.domain.exam.vo.ExamVO;
import com.fly.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {

    @Autowired
    private IExamService examService;


    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        List<ExamVO> list = examService.list(examQueryDTO);
        return getTableDataInfo(list);
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody ExamAddDTO examAddDTO) {
        return R.success(examService.add(examAddDTO) + "");
    }

    @PostMapping("/question/add")
    public R<Void> questionAdd(@RequestBody ExamQuestionDTO examQuestionDTO) {
        return toR(examService.questionAdd(examQuestionDTO));
    }

    @DeleteMapping("/question/delete")
    public R<Void> questionDelete(Long examId, Long questionId) {
        return toR(examService.questionDelete(examId, questionId));
    }

    @GetMapping("/detail")
    public R<ExamDetailVO> detail(Long examId) {
        return R.success(examService.detail(examId));
    }

    @PutMapping ("/edit")
    public R<Void> edit(@RequestBody ExamEditDTO examEditDTO) {
        return toR(examService.edit(examEditDTO));
    }

    @DeleteMapping("/delete")
    public R<Void> delete(Long examId) {
       return toR(examService.delete(examId));
    }

    @PutMapping("/publish")
    public R<Void> publish(Long examId) {
        return toR(examService.publish(examId));
    }

    @PutMapping("/cancelPublish")
    public R<Void> cancelPublish(Long examId) {
        return toR(examService.cancelPublish(examId));
    }

}
