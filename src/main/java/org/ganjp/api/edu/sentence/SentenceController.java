package org.ganjp.api.edu.sentence;

import lombok.RequiredArgsConstructor;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.core.model.ApiResponse;
import org.ganjp.api.core.model.PaginatedResponse;
import org.ganjp.api.edu.common.EduLearningItemResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/open/edu-sentences")
@RequiredArgsConstructor
public class SentenceController {
    private final SentenceService service;

    @GetMapping
    public ApiResponse<PaginatedResponse<EduLearningItemResponse>> getSentences(
            @RequestParam String channel, @RequestParam(required = false) String name,
            @RequestParam(required = false) String lang, @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean isActive, @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer week, @RequestParam(required = false) String difficultyLevel,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "displayOrder") String sort, @RequestParam(defaultValue = "asc") String direction) {
        Sentence.Language language = CmsUtil.parseLanguage(lang, Sentence.Language.class);
        if (lang != null && !lang.isBlank() && language == null) return ApiResponse.error(400, "Invalid lang", null);
        return ApiResponse.success(service.getSentences(channel, name, language, tags, isActive, term, week,
                difficultyLevel, page, size, sort, direction), "Sentences retrieved");
    }

    @GetMapping("/all")
    public ApiResponse<List<EduLearningItemResponse>> getAllSentences(
            @RequestParam String channel, @RequestParam(required = false) String name,
            @RequestParam(required = false) String lang, @RequestParam(required = false) String tags,
            @RequestParam(defaultValue = "true") Boolean isActive, @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer week, @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String updatedAfter) {
        Sentence.Language language = CmsUtil.parseLanguage(lang, Sentence.Language.class);
        return ApiResponse.success(service.getAllSentences(channel, name, language, tags, isActive, term, week,
                difficultyLevel, updatedAfter), "All sentences retrieved");
    }

    @GetMapping("/{id:[a-f0-9\\-]{36}}")
    public ApiResponse<EduLearningItemResponse> getSentenceById(@PathVariable String id) {
        EduLearningItemResponse response = service.getSentenceById(id);
        return response == null ? ApiResponse.error(404, "Sentence not found", null)
                : ApiResponse.success(response, "Sentence retrieved");
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
