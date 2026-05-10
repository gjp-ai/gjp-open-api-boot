package org.ganjp.api.cms.file;

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
import java.util.List;

@RestController
@RequestMapping("/open/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;

    @GetMapping
    public ApiResponse<PaginatedResponse<FileResponse>> getFiles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "displayOrder") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        org.ganjp.api.cms.file.File.Language language = CmsUtil.parseLanguage(lang,
                org.ganjp.api.cms.file.File.Language.class);
        if (lang != null && !lang.isBlank() && language == null) {
            return ApiResponse.error(400, "Invalid lang", null);
        }
        return ApiResponse.success(
                fileService.getFiles(name, language, tags, isActive, page, size, sort, direction),
                "Files retrieved");
    }

    @GetMapping("/all")
    public ApiResponse<List<FileResponse>> getAllFiles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String updatedAfter,
            @RequestParam(defaultValue = "true") Boolean isActive) {
        org.ganjp.api.cms.file.File.Language language = CmsUtil.parseLanguage(lang,
                org.ganjp.api.cms.file.File.Language.class);
        return ApiResponse.success(
                fileService.getAllFiles(name, language, tags, isActive, updatedAfter),
                "All files retrieved");
    }

    @GetMapping("/{id:[a-f0-9\\-]{36}}")
    public ApiResponse<FileResponse> getFileById(@PathVariable String id) {
        FileResponse resp = fileService.getFileById(id);
        if (resp == null) {
            return ApiResponse.error(404, "File not found", null);
        }
        return ApiResponse.success(resp, "File retrieved");
    }

    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> viewFile(@PathVariable String filename) {
        try {
            CmsUtil.validateFilename(filename);
            File file = fileService.getFileResource(filename);
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(CmsUtil.determineContentType(filename)))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentLength(file.length())
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            log.error("Error reading file: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
