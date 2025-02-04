package com.fly.system.service.exam;

import com.fly.system.domain.exam.dto.ExamAddDTO;
import com.fly.system.domain.exam.dto.ExamQueryDTO;
import com.fly.system.domain.exam.dto.ExamQuestionDTO;
import com.fly.system.domain.exam.vo.ExamVO;

import java.util.List;

public interface IExamService {
    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    int add(ExamAddDTO examAddDTO);

    boolean questionAdd(ExamQuestionDTO examQuestionDTO);
}
