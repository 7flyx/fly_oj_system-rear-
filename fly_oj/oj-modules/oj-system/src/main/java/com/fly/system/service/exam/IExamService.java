package com.fly.system.service.exam;

import com.fly.system.domain.exam.dto.ExamAddDTO;
import com.fly.system.domain.exam.dto.ExamEditDTO;
import com.fly.system.domain.exam.dto.ExamQueryDTO;
import com.fly.system.domain.exam.dto.ExamQuestionDTO;
import com.fly.system.domain.exam.vo.ExamDetailVO;
import com.fly.system.domain.exam.vo.ExamVO;

import java.util.List;

public interface IExamService {
    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    Long add(ExamAddDTO examAddDTO);

    boolean questionAdd(ExamQuestionDTO examQuestionDTO);

    ExamDetailVO detail(Long examId);

    int edit(ExamEditDTO examEditDTO);

    int questionDelete(Long examId, Long questionId);

    int delete(Long examId);

    int publish(Long examId);

    int cancelPublish(Long examId);
}
