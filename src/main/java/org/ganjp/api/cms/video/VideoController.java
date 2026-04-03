package org.ganjp.api.cms.video;

import lombok.RequiredArgsConstructor;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.core.model.ApiResponse;
import org.ganjp.api.core.model.PaginatedResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @GetMapping
    public ApiResponse<PaginatedResponse<VideoResponse>> getVideos(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "displayOrder") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Video.Language language = CmsUtil.parseLanguage(lang, Video.Language.class);
        if (lang != null && !lang.isBlank() && language == null) {
            return ApiResponse.error(400, "Invalid lang", null);
        }
        return ApiResponse.success(
                videoService.getVideos(name, language, tags, isActive, page, size, sort, direction),
                "Videos retrieved");
    }

    @GetMapping("/{id}")
    public ApiResponse<VideoResponse> getVideoById(@PathVariable String id) {
        VideoResponse resp = videoService.getVideoById(id);
        if (resp == null) return ApiResponse.error(404, "Video not found", null);
        return ApiResponse.success(resp, "Video retrieved");
    }
}
