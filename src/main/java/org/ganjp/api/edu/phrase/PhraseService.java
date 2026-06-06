package org.ganjp.api.edu.phrase;

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
public class PhraseService {
    private final PhraseRepository repository;
    private final PhraseUploadProperties uploadProperties;
    private final EduFileService fileService;

    @Value("${edu.phrase.audio.base-url:}")
    private String audioBaseUrl;

    public PaginatedResponse<EduLearningItemResponse> getPhrases(String channel, String name, Phrase.Language lang,
            String tags, Boolean isActive, Integer term, Integer week, String difficultyLevel,
            int page, int size, String sort, String direction) {
        Page<Phrase> result = repository.search(channel, name, lang, tags, isActive, term, week, difficultyLevel,
                CmsUtil.buildPageable(page, size, sort, direction));
        List<EduLearningItemResponse> list = result.getContent().stream().map(this::map).toList();
        return PaginatedResponse.of(list, result.getNumber(), result.getSize(), result.getTotalElements());
    }

    public List<EduLearningItemResponse> getAllPhrases(String channel, String name, Phrase.Language lang,
            String tags, Boolean isActive, Integer term, Integer week, String difficultyLevel, String updatedAfter) {
        return repository.findAll(channel, name, lang, tags, isActive, term, week, difficultyLevel,
                CmsUtil.parseLocalDateTime(updatedAfter)).stream().map(this::map).toList();
    }

    public EduLearningItemResponse getPhraseById(String id) {
        return repository.findById(id).map(this::map).orElse(null);
    }

    public File getAudioFile(String filename) {
        if (!repository.existsByPhoneticAudioFilename(filename)) throw new IllegalArgumentException("Phrase audio not found");
        return fileService.resolveExistingFile(uploadProperties.getAudioDirectory(), filename);
    }

    private EduLearningItemResponse map(Phrase p) {
        return EduLearningItemResponse.builder()
                .id(p.getId()).name(p.getName()).phonetic(p.getPhonetic())
                .phoneticAudioFilename(p.getPhoneticAudioFilename())
                .phoneticAudioUrl(CmsUtil.joinBaseAndPath(audioBaseUrl, p.getPhoneticAudioFilename()))
                .phoneticAudioOriginalUrl(p.getPhoneticAudioOriginalUrl()).synonyms(p.getSynonyms())
                .translation(p.getTranslation()).meaningClue(p.getMeaningClue()).meaning(p.getMeaning())
                .easyMeaning(p.getEasyMeaning()).sentenceOne(p.getSentenceOne()).sentenceTwo(p.getSentenceTwo())
                .difficultyLevel(p.getDifficultyLevel()).dictionaryUrl(p.getDictionaryUrl()).term(p.getTerm())
                .week(p.getWeek()).channel(p.getChannel()).tags(p.getTags())
                .lang(p.getLang() != null ? p.getLang().name() : null).displayOrder(p.getDisplayOrder())
                .updatedAt(p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null).build();
    }
}
