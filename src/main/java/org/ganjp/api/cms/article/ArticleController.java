package org.ganjp.api.cms.article;

import lombok.RequiredArgsConstructor;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.core.model.ApiResponse;
import org.ganjp.api.core.model.PaginatedResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping
    public ApiResponse<PaginatedResponse<ArticleResponse>> getArticles(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "displayOrder") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Article.Language language = CmsUtil.parseLanguage(lang, Article.Language.class);
        if (lang != null && !lang.isBlank() && language == null)
            return ApiResponse.error(400, "Invalid lang", null);
        return ApiResponse.success(
                articleService.getArticles(title, language, tags, isActive, page, size, sort, direction),
                "Articles retrieved");
    }

    @GetMapping("/{id}")
    public ApiResponse<ArticleDetailResponse> getArticleById(@PathVariable String id) {
        ArticleDetailResponse resp = articleService.getArticleById(id);
        if (resp == null) {
            return ApiResponse.error(404, "Article not found", null);
        }
        return ApiResponse.success(resp, "Article retrieved");
    }
}
