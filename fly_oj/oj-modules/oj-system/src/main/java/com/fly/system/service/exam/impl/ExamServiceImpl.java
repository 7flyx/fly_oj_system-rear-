package com.fly.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.common.core.constants.Constants;
import com.fly.common.core.enums.ResultCode;
import com.fly.common.security.exception.ServiceException;
import com.fly.system.domain.exam.Exam;
import com.fly.system.domain.exam.ExamQuestion;
import com.fly.system.domain.exam.dto.ExamAddDTO;
import com.fly.system.domain.exam.dto.ExamEditDTO;
import com.fly.system.domain.exam.dto.ExamQueryDTO;
import com.fly.system.domain.exam.dto.ExamQuestionDTO;
import com.fly.system.domain.exam.vo.ExamDetailVO;
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

import java.time.LocalDateTime;
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
    public Long add(ExamAddDTO examAddDTO) {
        checkExamSaveParams(examAddDTO, null);
        Exam exam = new Exam();
        BeanUtil.copyProperties(examAddDTO, exam);
        examMapper.insert(exam);
        return exam.getExamId();
    }


    @Override
    public boolean questionAdd(ExamQuestionDTO examQuestionDTO) {
        Exam exam = getExam(examQuestionDTO.getExamId());
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

    @Override
    public int questionDelete(Long examId, Long questionId) {
        Exam exam = getExam(examId);
        checkExam(exam);
        return examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId)
                .eq(ExamQuestion::getQuestionId, questionId));

    }

    @Override
    public ExamDetailVO detail(Long examId) {
        ExamDetailVO result = new ExamDetailVO();
        Exam exam = getExam(examId);
        // 竞赛的基本信息
        BeanUtil.copyProperties(exam, result);
        List<ExamQuestion> examQuestionList = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
                .select(ExamQuestion::getQuestionId)
                .eq(ExamQuestion::getExamId, examId));
        if (CollectionUtil.isEmpty(examQuestionList)) {
            // 返回详情
            return result;
        }
        // 竞赛的题目信息
        List<Long> questionIdList = examQuestionList.stream().map(ExamQuestion::getQuestionId).toList();
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId, Question::getTitle, Question::getDifficulty)
                .in(Question::getQuestionId, questionIdList));
        List<QuestionVO> questionVOList = BeanUtil.copyToList(questions, QuestionVO.class);
        result.setExamQuestionList(questionVOList);
        return result;
    }

    @Override
    public int edit(ExamEditDTO examEditDTO) {
        Exam exam = getExam(examEditDTO.getExamId());
        checkExamSaveParams(examEditDTO, examEditDTO.getExamId()); // 检查参数的合法性

        exam.setTitle(examEditDTO.getTitle());
        exam.setStartTime(examEditDTO.getStartTime());
        exam.setEndTime(examEditDTO.getEndTime());
        return examMapper.updateById(exam);
    }

    private void checkExam(Exam exam) {
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_STARED);
        }
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

    private Exam getExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        return exam;
    }

    private void checkExamSaveParams(ExamAddDTO examSaveDTO, Long examId) {
        // 开始时间 居然 晚于 结束时间，抛异常
        if (examSaveDTO.getStartTime().isAfter(examSaveDTO.getEndTime())) {
            throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }
        List<Exam> exams = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .eq(Exam::getTitle, examSaveDTO.getTitle())
                .ne(Exam::getExamId, examId));
        if (exams.size() != 0) { // 竞赛名称重复了
            throw new ServiceException(ResultCode.EXAM_TITLE_ALREADY_EXISTS);
        }
    }

    @Override
    public int delete(Long examId) {
        Exam exam = getExam(examId);
        checkExam(exam);
        // 删除竞赛中的题目信息
        examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId));
        // 删除竞赛
        return examMapper.deleteById(exam);
    }

    @Override
    public int publish(Long examId) {
        Exam exam = getExam(examId);
        if (exam.getEndTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_STARED);
        }
        Long count = examQuestionMapper.selectCount(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId));
        if (count == null || count <= 0) { // 竞赛中没有任何题目
            throw new ServiceException(ResultCode.EXAM_NOT_HAS_QUESTION);
        }
        exam.setStatus(Constants.TRUE);

        return examMapper.updateById(exam);
    }

    @Override
    public int cancelPublish(Long examId) {
        Exam exam = getExam(examId);
        checkExam(exam); // 检查竞赛是否已经开始了
        exam.setStatus(Constants.FALSE);
        return examMapper.updateById(exam);
    }


}
