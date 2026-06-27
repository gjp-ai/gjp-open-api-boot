package org.ganjp.api.edu.question;

import lombok.RequiredArgsConstructor;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.core.model.PaginatedResponse;
import org.ganjp.api.edu.common.EduFavoriteTagUtil;
import org.ganjp.api.edu.common.EduQuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FreeTextQuestionService {
    private final FreeTextQuestionRepository repository;

    public PaginatedResponse<EduQuestionResponse> getQuestions(String channel, String question, FreeTextQuestion.Language lang,
            String tags, Boolean isActive, Integer term, Integer week, String difficultyLevel, String gradeLevel,
            String subject, String topic, int page, int size, String sort, String direction) {
        Page<FreeTextQuestion> result = repository.search(channel, question, lang, tags, isActive, term, week,
                difficultyLevel, gradeLevel, subject, topic, CmsUtil.buildPageable(page, size, sort, direction));
        return PaginatedResponse.of(result.getContent().stream().map(EduQuestionMapper::from).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements());
    }

    public List<EduQuestionResponse> getAllQuestions(String channel, String question, FreeTextQuestion.Language lang,
            String tags, Boolean isActive, Integer term, Integer week, String difficultyLevel, String gradeLevel,
            String subject, String topic, String updatedAfter) {
        return repository.findAll(channel, question, lang, tags, isActive, term, week, difficultyLevel, gradeLevel,
                subject, topic, CmsUtil.parseLocalDateTime(updatedAfter)).stream().map(EduQuestionMapper::from).toList();
    }

    public EduQuestionResponse getQuestionById(String id) {
        return repository.findById(id).map(EduQuestionMapper::from).orElse(null);
    }

    @Transactional
    public EduQuestionResponse toggleFavoriteTag(String id) {
        return repository.findById(id).map(q -> {
            q.setTags(EduFavoriteTagUtil.toggleFavoriteTag(q.getTags(), q.getLang()));
            q.setUpdatedAt(LocalDateTime.now());
            return EduQuestionMapper.from(repository.save(q));
        }).orElse(null);
    }
}
