package org.ganjp.api.edu.vocabulary;

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
public class VocabularyService {
    private final VocabularyRepository repository;
    private final VocabularyUploadProperties uploadProperties;
    private final EduFileService fileService;

    @Value("${edu.vocabulary.audio.base-url:}")
    private String audioBaseUrl;

    public PaginatedResponse<EduLearningItemResponse> getVocabularies(String channel, String name, Vocabulary.Language lang,
            String tags, Boolean isActive, Integer term, Integer week, String difficultyLevel, String partOfSpeech,
            int page, int size, String sort, String direction) {
        Page<Vocabulary> result = repository.search(channel, name, lang, tags, isActive, term, week, difficultyLevel,
                partOfSpeech, CmsUtil.buildPageable(page, size, sort, direction));
        List<EduLearningItemResponse> list = result.getContent().stream().map(this::map).toList();
        return PaginatedResponse.of(list, result.getNumber(), result.getSize(), result.getTotalElements());
    }

    public List<EduLearningItemResponse> getAllVocabularies(String channel, String name, Vocabulary.Language lang,
            String tags, Boolean isActive, Integer term, Integer week, String difficultyLevel, String partOfSpeech,
            String updatedAfter) {
        return repository.findAll(channel, name, lang, tags, isActive, term, week, difficultyLevel, partOfSpeech,
                CmsUtil.parseLocalDateTime(updatedAfter)).stream().map(this::map).toList();
    }

    public EduLearningItemResponse getVocabularyById(String id) {
        return repository.findById(id).map(this::map).orElse(null);
    }

    public File getAudioFile(String filename) {
        if (!repository.existsByPhoneticUsAudioFilenameOrPhoneticUkAudioFilename(filename, filename)) {
            throw new IllegalArgumentException("Vocabulary audio not found: " + filename);
        }
        return fileService.resolveExistingFile(uploadProperties.getAudioDirectory(), filename);
    }

    private EduLearningItemResponse map(Vocabulary v) {
        return EduLearningItemResponse.builder()
                .id(v.getId()).name(v.getName())
                .phoneticUs(v.getPhoneticUs()).phoneticUsAudioFilename(v.getPhoneticUsAudioFilename())
                .phoneticUsAudioUrl(CmsUtil.joinBaseAndPath(audioBaseUrl, v.getPhoneticUsAudioFilename()))
                .phoneticUsAudioOriginalUrl(v.getPhoneticUsAudioOriginalUrl())
                .phoneticUk(v.getPhoneticUk()).phoneticUkAudioFilename(v.getPhoneticUkAudioFilename())
                .phoneticUkAudioUrl(CmsUtil.joinBaseAndPath(audioBaseUrl, v.getPhoneticUkAudioFilename()))
                .phoneticUkAudioOriginalUrl(v.getPhoneticUkAudioOriginalUrl())
                .partOfSpeech(v.getPartOfSpeech()).synonyms(v.getSynonyms()).translation(v.getTranslation())
                .meaningClue(v.getMeaningClue()).meaning(v.getMeaning()).easyMeaning(v.getEasyMeaning())
                .sentenceOne(v.getSentenceOne()).sentenceTwo(v.getSentenceTwo())
                .difficultyLevel(v.getDifficultyLevel()).dictionaryUrl(v.getDictionaryUrl())
                .additionalInfo(v.getAdditionalInfo()).term(v.getTerm()).week(v.getWeek()).channel(v.getChannel())
                .tags(v.getTags()).lang(v.getLang() != null ? v.getLang().name() : null)
                .displayOrder(v.getDisplayOrder()).updatedAt(v.getUpdatedAt() != null ? v.getUpdatedAt().toString() : null)
                .build();
    }
}
