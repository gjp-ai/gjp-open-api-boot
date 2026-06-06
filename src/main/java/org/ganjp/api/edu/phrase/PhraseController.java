package org.ganjp.api.edu.phrase;

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
@RequestMapping("/open/edu-phrases")
@RequiredArgsConstructor
public class PhraseController {
    private final PhraseService service;

    @GetMapping
    public ApiResponse<PaginatedResponse<EduLearningItemResponse>> getPhrases(
            @RequestParam String channel, @RequestParam(required = false) String name,
            @RequestParam(required = false) String lang, @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean isActive, @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer week, @RequestParam(required = false) String difficultyLevel,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "displayOrder") String sort, @RequestParam(defaultValue = "asc") String direction) {
        Phrase.Language language = CmsUtil.parseLanguage(lang, Phrase.Language.class);
        if (lang != null && !lang.isBlank() && language == null) return ApiResponse.error(400, "Invalid lang", null);
        return ApiResponse.success(service.getPhrases(channel, name, language, tags, isActive, term, week,
                difficultyLevel, page, size, sort, direction), "Phrases retrieved");
    }

    @GetMapping("/all")
    public ApiResponse<List<EduLearningItemResponse>> getAllPhrases(
            @RequestParam String channel, @RequestParam(required = false) String name,
            @RequestParam(required = false) String lang, @RequestParam(required = false) String tags,
            @RequestParam(defaultValue = "true") Boolean isActive, @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Integer week, @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String updatedAfter) {
        Phrase.Language language = CmsUtil.parseLanguage(lang, Phrase.Language.class);
        return ApiResponse.success(service.getAllPhrases(channel, name, language, tags, isActive, term, week,
                difficultyLevel, updatedAfter), "All phrases retrieved");
    }

    @GetMapping("/{id:[a-f0-9\\-]{36}}")
    public ApiResponse<EduLearningItemResponse> getPhraseById(@PathVariable String id) {
        EduLearningItemResponse response = service.getPhraseById(id);
        return response == null ? ApiResponse.error(404, "Phrase not found", null)
                : ApiResponse.success(response, "Phrase retrieved");
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
