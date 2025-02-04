package com.fly.system.controller.exam;

import com.fly.common.core.controller.BaseController;
import com.fly.common.core.domain.R;
import com.fly.common.core.domain.TableDataInfo;
import com.fly.system.domain.exam.dto.ExamAddDTO;
import com.fly.system.domain.exam.dto.ExamQueryDTO;
import com.fly.system.domain.exam.dto.ExamQuestionDTO;
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
    public R<Void> add(@RequestBody ExamAddDTO examAddDTO) {
        return toR(examService.add(examAddDTO));
    }

    @PostMapping("/question/add")
    public R<Void> questionAdd(ExamQuestionDTO examQuestionDTO) {
        return toR(examService.questionAdd(examQuestionDTO));
    }
}
