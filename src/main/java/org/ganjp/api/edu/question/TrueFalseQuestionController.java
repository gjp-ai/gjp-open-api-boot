package org.ganjp.api.edu.question;

import lombok.RequiredArgsConstructor;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.core.model.ApiResponse;
import org.ganjp.api.core.model.PaginatedResponse;
import org.ganjp.api.edu.common.EduQuestionResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/open/edu-true-false-questions")
@RequiredArgsConstructor
public class TrueFalseQuestionController {
    private final TrueFalseQuestionService service;

    @GetMapping
    public ApiResponse<PaginatedResponse<EduQuestionResponse>> getQuestions(@RequestParam String channel,
            @RequestParam(required = false) String question, @RequestParam(required = false) String lang,
            @RequestParam(required = false) String tags, @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer term, @RequestParam(required = false) Integer week,
            @RequestParam(required = false) String difficultyLevel, @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String subject, @RequestParam(required = false) String topic,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "displayOrder") String sort, @RequestParam(defaultValue = "asc") String direction) {
        TrueFalseQuestion.Language language = CmsUtil.parseLanguage(lang, TrueFalseQuestion.Language.class);
        if (lang != null && !lang.isBlank() && language == null) return ApiResponse.error(400, "Invalid lang", null);
        return ApiResponse.success(service.getQuestions(channel, question, language, tags, isActive, term, week,
                difficultyLevel, gradeLevel, subject, topic, page, size, sort, direction), "True/false questions retrieved");
    }

    @GetMapping("/all")
    public ApiResponse<List<EduQuestionResponse>> getAllQuestions(@RequestParam String channel,
            @RequestParam(required = false) String question, @RequestParam(required = false) String lang,
            @RequestParam(required = false) String tags, @RequestParam(defaultValue = "true") Boolean isActive,
            @RequestParam(required = false) Integer term, @RequestParam(required = false) Integer week,
            @RequestParam(required = false) String difficultyLevel, @RequestParam(required = false) String gradeLevel,
            @RequestParam(required = false) String subject, @RequestParam(required = false) String topic,
            @RequestParam(required = false) String updatedAfter) {
        TrueFalseQuestion.Language language = CmsUtil.parseLanguage(lang, TrueFalseQuestion.Language.class);
        return ApiResponse.success(service.getAllQuestions(channel, question, language, tags, isActive, term, week,
                difficultyLevel, gradeLevel, subject, topic, updatedAfter), "All true/false questions retrieved");
    }

    @GetMapping("/{id:[a-f0-9\\-]{36}}")
    public ApiResponse<EduQuestionResponse> getQuestionById(@PathVariable String id) {
        EduQuestionResponse response = service.getQuestionById(id);
        return response == null ? ApiResponse.error(404, "True/false question not found", null)
                : ApiResponse.success(response, "True/false question retrieved");
    }
}
