package com.fly.system.elasticsearch;

import com.fly.system.domain.question.QuestionES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

// elasticsearch 查询相关的接口，对应的就是mapper那一层

@Repository
public interface QuestionRepository extends ElasticsearchRepository<QuestionES, Long> {


}
