package org.ganjp.api.cms.logo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.core.model.ApiResponse;
import org.ganjp.api.core.model.PaginatedResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/open/logos")
@RequiredArgsConstructor
@Slf4j
public class LogoController {
    private final LogoService logoService;

    @GetMapping
    public ApiResponse<PaginatedResponse<LogoResponse>> getLogos(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "displayOrder") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        Logo.Language language = CmsUtil.parseLanguage(lang, Logo.Language.class);
        if (lang != null && !lang.isBlank() && language == null) {
            return ApiResponse.error(400, "Invalid lang", null);
        }
        return ApiResponse.success(
                logoService.getLogos(name, language, tags, isActive, page, size, sort, direction),
                "Logos retrieved");
    }

    @GetMapping("/{id}")
    public ApiResponse<LogoResponse> getLogoById(@PathVariable String id) {
        LogoResponse resp = logoService.getLogoById(id);
        if (resp == null) {
            return ApiResponse.error(404, "Logo not found", null);
        }
        return ApiResponse.success(resp, "Logo retrieved");
    }

    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> viewLogo(@PathVariable String filename) {
        try {
            CmsUtil.validateFilename(filename);
            File file = logoService.getLogoFile(filename);
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(CmsUtil.determineContentType(filename)))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            log.error("Error reading logo file: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
