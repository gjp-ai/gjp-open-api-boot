package org.ganjp.api.cms.website;

import lombok.RequiredArgsConstructor;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.core.model.ApiResponse;
import org.ganjp.api.core.model.PaginatedResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/websites")
@RequiredArgsConstructor
public class WebsiteController {
    private final WebsiteService websiteService;

    @GetMapping
    public ApiResponse<PaginatedResponse<WebsiteResponse>> getWebsites(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "displayOrder") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Website.Language language = CmsUtil.parseLanguage(lang, Website.Language.class);
        if (lang != null && !lang.isBlank() && language == null) {
            return ApiResponse.error(400, "Invalid lang", null);
        }
        return ApiResponse.success(
                websiteService.getWebsites(name, language, tags, isActive, page, size, sort, direction),
                "Websites retrieved");
    }

    @GetMapping("/{id}")
    public ApiResponse<WebsiteResponse> getWebsiteById(@PathVariable String id) {
        WebsiteResponse resp = websiteService.getWebsiteById(id);
        if (resp == null) {
            return ApiResponse.error(404, "Website not found", null);
        }
        return ApiResponse.success(resp, "Website retrieved");
    }
}
