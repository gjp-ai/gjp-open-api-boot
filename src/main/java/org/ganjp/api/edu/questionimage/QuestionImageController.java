package org.ganjp.api.edu.questionimage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.core.model.ApiResponse;
import org.ganjp.api.edu.common.EduQuestionImageResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/open/edu-question-images")
@RequiredArgsConstructor
public class QuestionImageController {
    private final QuestionImageService service;

    @GetMapping
    public ApiResponse<List<EduQuestionImageResponse>> search(
            @RequestParam(required = false) String multipleChoiceQuestionId,
            @RequestParam(required = false) String freeTextQuestionId,
            @RequestParam(required = false) String trueFalseQuestionId,
            @RequestParam(required = false) String fillBlankQuestionId,
            @RequestParam(required = false) String lang,
            @RequestParam(defaultValue = "true") Boolean isActive) {
        QuestionImage.Language language = CmsUtil.parseLanguage(lang, QuestionImage.Language.class);
        if (lang != null && !lang.isBlank() && language == null) return ApiResponse.error(400, "Invalid lang", null);
        return ApiResponse.success(service.search(multipleChoiceQuestionId, freeTextQuestionId, trueFalseQuestionId,
                fillBlankQuestionId, language, isActive), "Question images retrieved");
    }

    @GetMapping("/{id:[a-f0-9\\-]{36}}")
    public ApiResponse<EduQuestionImageResponse> getById(@PathVariable String id) {
        EduQuestionImageResponse response = service.getById(id);
        return response == null ? ApiResponse.error(404, "Question image not found", null)
                : ApiResponse.success(response, "Question image retrieved");
    }

    @GetMapping("/view/{filename}")
    public ResponseEntity<FileSystemResource> viewImage(@PathVariable String filename) {
        try {
            CmsUtil.validateFilename(filename);
            java.io.File file = service.getImageFile(filename);
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(CmsUtil.determineContentType(filename)))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(new FileSystemResource(file));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
