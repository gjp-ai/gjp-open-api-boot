package org.ganjp.api.edu.sentence;

import lombok.RequiredArgsConstructor;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.core.model.PaginatedResponse;
import org.ganjp.api.edu.common.EduFileService;
import org.ganjp.api.edu.common.EduLearningItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SentenceService {
    private final SentenceRepository repository;
    private final SentenceUploadProperties uploadProperties;
    private final EduFileService fileService;

    @Value("${edu.sentence.audio.base-url:}")
    private String audioBaseUrl;

    public PaginatedResponse<EduLearningItemResponse> getSentences(String channel, String name, Sentence.Language lang,
            String tags, Boolean isActive, Integer term, Integer week, String difficultyLevel,
            int page, int size, String sort, String direction) {
        Page<Sentence> result = repository.search(channel, name, lang, tags, isActive, term, week, difficultyLevel,
                CmsUtil.buildPageable(page, size, sort, direction));
        List<EduLearningItemResponse> list = result.getContent().stream().map(this::map).toList();
        return PaginatedResponse.of(list, result.getNumber(), result.getSize(), result.getTotalElements());
    }

    public List<EduLearningItemResponse> getAllSentences(String channel, String name, Sentence.Language lang,
            String tags, Boolean isActive, Integer term, Integer week, String difficultyLevel, String updatedAfter) {
        return repository.findAll(channel, name, lang, tags, isActive, term, week, difficultyLevel,
                CmsUtil.parseLocalDateTime(updatedAfter)).stream().map(this::map).toList();
    }

    public EduLearningItemResponse getSentenceById(String id) {
        return repository.findById(id).map(this::map).orElse(null);
    }

    public File getAudioFile(String filename) {
        if (!repository.existsByPhoneticAudioFilename(filename)) throw new IllegalArgumentException("Sentence audio not found");
        return fileService.resolveExistingFile(uploadProperties.getAudioDirectory(), filename);
    }

    private EduLearningItemResponse map(Sentence s) {
        return EduLearningItemResponse.builder()
                .id(s.getId()).name(s.getName()).phonetic(s.getPhonetic())
                .phoneticAudioFilename(s.getPhoneticAudioFilename())
                .phoneticAudioUrl(CmsUtil.joinBaseAndPath(audioBaseUrl, s.getPhoneticAudioFilename()))
                .translation(s.getTranslation()).explanation(s.getExplanation()).difficultyLevel(s.getDifficultyLevel())
                .term(s.getTerm()).week(s.getWeek()).channel(s.getChannel()).tags(s.getTags())
                .lang(s.getLang() != null ? s.getLang().name() : null).displayOrder(s.getDisplayOrder())
                .updatedAt(s.getUpdatedAt() != null ? s.getUpdatedAt().toString() : null).build();
    }
}
