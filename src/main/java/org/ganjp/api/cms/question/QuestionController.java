package org.ganjp.api.cms.question;

import lombok.RequiredArgsConstructor;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.core.model.ApiResponse;
import org.ganjp.api.core.model.PaginatedResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/open/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public ApiResponse<PaginatedResponse<QuestionResponse>> getQuestions(
            @RequestParam(required = false) String question,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "displayOrder") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Question.Language language = CmsUtil.parseLanguage(lang, Question.Language.class);
        if (lang != null && !lang.isBlank() && language == null) {
            return ApiResponse.error(400, "Invalid lang", null);
        }
        return ApiResponse.success(
                questionService.getQuestions(question, language, tags, isActive, page, size, sort, direction),
                "Questions retrieved");
    }

    @GetMapping("/all")
    public ApiResponse<List<QuestionResponse>> getAllQuestions(
            @RequestParam(required = false) String question,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String updatedAfter,
            @RequestParam(defaultValue = "true") Boolean isActive) {
        Question.Language language = CmsUtil.parseLanguage(lang, Question.Language.class);
        return ApiResponse.success(
                questionService.getAllQuestions(question, language, tags, isActive, updatedAfter),
                "All questions retrieved");
    }

    @GetMapping("/{id:[a-f0-9\\-]{36}}")
    public ApiResponse<QuestionResponse> getQuestionById(@PathVariable String id) {
        QuestionResponse resp = questionService.getQuestionById(id);
        if (resp == null) {
            return ApiResponse.error(404, "Question not found", null);
        }
        return ApiResponse.success(resp, "Question retrieved");
    }
}
