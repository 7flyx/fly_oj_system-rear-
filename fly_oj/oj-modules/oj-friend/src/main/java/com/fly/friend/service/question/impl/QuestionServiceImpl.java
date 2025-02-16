package com.fly.friend.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fly.common.core.domain.TableDataInfo;
import com.fly.friend.domain.question.Question;
import com.fly.friend.domain.question.QuestionQueryDTO;
import com.fly.friend.domain.question.es.QuestionES;
import com.fly.friend.domain.question.vo.QuestionDetailVO;
import com.fly.friend.domain.question.vo.QuestionVO;
import com.fly.friend.elasticsearch.QuestionRepository;
import com.fly.friend.manager.QuestionCacheManager;
import com.fly.friend.mapper.question.QuestionMapper;
import com.fly.friend.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements IQuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionCacheManager questionCacheManager;
    @Override
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        long count = questionRepository.count();
        if (count <= 0) {
            refreshQuestion();
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(questionQueryDTO.getPageNum() - 1, questionQueryDTO.getPageSize(), sort);
        Integer difficulty = questionQueryDTO.getDifficulty();
        String keyword = questionQueryDTO.getKeyword();

        Page<QuestionES> questionESPage;
        if (difficulty == null && StrUtil.isEmpty(keyword)) {
            questionESPage = questionRepository.findAll(pageable);
        } else if (StrUtil.isEmpty(keyword)) {
            questionESPage = questionRepository.findQuestionByDifficulty(difficulty, pageable);
        } else if (difficulty == null) {
            questionESPage = questionRepository.findByTitleOrContent(keyword, keyword, pageable);
        } else {
            questionESPage =  questionRepository.findByTitleOrContentAndDifficulty(keyword, keyword, difficulty, pageable);
        }
        long total = questionESPage.getTotalElements();
        if (total == 0) {
            return TableDataInfo.empty();
        }

        List<QuestionES> questionList = questionESPage.getContent();
        List<QuestionVO> questionVOS = BeanUtil.copyToList(questionList, QuestionVO.class);
        return TableDataInfo.success(questionVOS, total);
    }

    @Override
    public QuestionDetailVO detail(Long questionId) {
        QuestionES questionES = questionRepository.findById(questionId).orElse(null);
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        if (questionES != null) { // elasticsearch中有数据，直接返回
            BeanUtil.copyProperties(questionES, questionDetailVO);
            return questionDetailVO;
        }
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            return null;
        }
        refreshQuestion();
        BeanUtil.copyProperties(question, questionDetailVO);

        return questionDetailVO;
    }

    @Override
    public String preQuestion(Long questionId) {
        Long listSize = questionCacheManager.getListSize();
        if (listSize == null || listSize <= 0) {
            questionCacheManager.refreshCache();
        }
        return questionCacheManager.preQuestion(questionId).toString();
    }

    @Override
    public String nextQuestion(Long questionId) {
        Long listSize = questionCacheManager.getListSize();
        if (listSize == null || listSize <= 0) {
            questionCacheManager.refreshCache();
        }
        return questionCacheManager.nextQuestion(questionId).toString();
    }

    private void refreshQuestion() {
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>());
        if (CollectionUtil.isEmpty(questions)) {
            return;
        }
        List<QuestionES> questionES = BeanUtil.copyToList(questions, QuestionES.class);
        // 将数据库的数据 加载到 elasticsearch中去
        questionRepository.saveAll(questionES);
    }
}
