package com.fly.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.common.core.enums.ResultCode;
import com.fly.common.security.exception.ServiceException;
import com.fly.system.domain.exam.Exam;
import com.fly.system.domain.exam.ExamQuestion;
import com.fly.system.domain.exam.dto.ExamAddDTO;
import com.fly.system.domain.exam.dto.ExamQueryDTO;
import com.fly.system.domain.exam.dto.ExamQuestionDTO;
import com.fly.system.domain.exam.vo.ExamVO;
import com.fly.system.domain.question.Question;
import com.fly.system.domain.question.vo.QuestionVO;
import com.fly.system.mapper.exam.ExamMapper;
import com.fly.system.mapper.exam.ExamQuestionMapper;
import com.fly.system.mapper.question.QuestionMapper;
import com.fly.system.service.exam.IExamService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements IExamService {

    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ExamQuestionMapper examQuestionMapper;
    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        // 分页之后的剩余的数据，不是总的数据量
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        List<ExamVO> examVOS = examMapper.selectExamList(examQueryDTO);
        return examVOS;
    }

    @Override
    public int add(ExamAddDTO examAddDTO) {
        // 开始时间 居然 晚于 结束时间，抛异常
        if (examAddDTO.getStartTime().isAfter(examAddDTO.getEndTime())) {
            throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }
        List<Exam> exams = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .eq(Exam::getTitle, examAddDTO.getTitle()));
        if(exams != null || exams.size() != 0) { // 竞赛名称重复了
            throw new ServiceException(ResultCode.EXAM_TITIE_ALREADY_EXISTS);
        }
        Exam exam = new Exam();
        BeanUtil.copyProperties(examAddDTO, exam);
        return examMapper.insert(exam);
    }

    @Override
    public boolean questionAdd(ExamQuestionDTO examQuestionDTO) {
        Exam exam = getExam(examQuestionDTO);
        Set<Long> questionIdSet = examQuestionDTO.getQuestionIdSet();
        if (CollectionUtil.isEmpty(questionIdSet)) {
            return true;
        }
        List<Question> questions = questionMapper.selectBatchIds(questionIdSet);
        if (CollectionUtil.isEmpty(questions) || questions.size() < questionIdSet.size()) {
            throw new ServiceException(ResultCode.EXAM_QUESTION_NOT_EXISTS);
        }
        return saveExamQuestion(exam, questionIdSet);
    }

    private boolean saveExamQuestion(Exam exam, Set<Long> questionIdSet) {
        int num = 1;
        List<ExamQuestion> examQuestionList = new ArrayList<>();
        for (Long questionId : questionIdSet) {
//            Question question = questionMapper.selectById(questionId);
//            if (question == null) { // 添加竞赛的题目不存在
//                throw new ServiceException(ResultCode.EXAM_QUESTION_NOT_EXISTS);
//            }
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExamId(exam.getExamId());
            examQuestion.setQuestionId(questionId);
            examQuestion.setQuestionOrder(num++);
            examQuestionList.add(examQuestion);
//            examQuestionMapper.insert(examQuestion);
        }
        // 一次性插入多条数据
        return saveBatch(examQuestionList); // 来自 Mybatis plus的扩展模块
    }

    private Exam getExam(ExamQuestionDTO examQuestionDTO) {
        Exam exam = examMapper.selectById(examQuestionDTO.getExamId());
        if (exam == null) {
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }
        return exam;
    }
}
