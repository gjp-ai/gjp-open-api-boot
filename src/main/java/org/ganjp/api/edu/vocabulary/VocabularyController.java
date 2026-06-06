package org.ganjp.api.edu.vocabulary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.core.model.ApiResponse;
import org.ganjp.api.core.model.PaginatedResponse;
import org.ganjp.api.edu.common.EduLearningItemResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/open/edu-vocabularies")
@RequiredArgsConstructor
public class VocabularyController {
    private final VocabularyService service;

    @GetMapping
    public ApiResponse<PaginatedResponse<EduLearningItemResponse>> getVocabularies(
            @RequestParam String channel, @RequestParam(required = false) String name,
            @RequestParam(required = false) String lang, @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean isActive, @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer week, @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String partOfSpeech, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "displayOrder") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Vocabulary.Language language = CmsUtil.parseLanguage(lang, Vocabulary.Language.class);
        if (lang != null && !lang.isBlank() && language == null) return ApiResponse.error(400, "Invalid lang", null);
        return ApiResponse.success(service.getVocabularies(channel, name, language, tags, isActive, term, week,
                difficultyLevel, partOfSpeech, page, size, sort, direction), "Vocabularies retrieved");
    }

    @GetMapping("/all")
    public ApiResponse<List<EduLearningItemResponse>> getAllVocabularies(
            @RequestParam String channel, @RequestParam(required = false) String name,
            @RequestParam(required = false) String lang, @RequestParam(required = false) String tags,
            @RequestParam(defaultValue = "true") Boolean isActive, @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer week, @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String partOfSpeech, @RequestParam(required = false) String updatedAfter) {
        Vocabulary.Language language = CmsUtil.parseLanguage(lang, Vocabulary.Language.class);
        return ApiResponse.success(service.getAllVocabularies(channel, name, language, tags, isActive, term, week,
                difficultyLevel, partOfSpeech, updatedAfter), "All vocabularies retrieved");
    }

    @GetMapping("/{id:[a-f0-9\\-]{36}}")
    public ApiResponse<EduLearningItemResponse> getVocabularyById(@PathVariable String id) {
        EduLearningItemResponse response = service.getVocabularyById(id);
        return response == null ? ApiResponse.error(404, "Vocabulary not found", null)
                : ApiResponse.success(response, "Vocabulary retrieved");
    }

    @GetMapping("/audios/{filename}")
    public ResponseEntity<FileSystemResource> viewAudio(@PathVariable String filename) {
        try {
            CmsUtil.validateFilename(filename);
            java.io.File file = service.getAudioFile(filename);
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(CmsUtil.determineContentType(filename)))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(new FileSystemResource(file));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
