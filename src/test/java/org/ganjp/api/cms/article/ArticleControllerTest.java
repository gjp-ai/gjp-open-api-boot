package org.ganjp.api.cms.article;

import org.ganjp.api.core.model.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

        @Autowired
        private MockMvc mockMvc;

        // Use MockitoBean instead of MockBean for Spring Boot 3.4+ compatibility
        @MockitoBean
        private ArticleService articleService;

        @Test
        void should_returnPaginatedArticles_when_getArticles() throws Exception {
                // Given
                ArticleResponse articleResponse = ArticleResponse.builder()
                                .id("abc-123")
                                .title("Controller Test")
                                .build();

                PaginatedResponse<ArticleResponse> paginatedData = PaginatedResponse.of(
                                List.of(articleResponse), 0, 20, 1);

                when(articleService.getArticles(isNull(), isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt(), anyString(),
                                anyString()))
                                .thenReturn(paginatedData);

                // When & Then
                mockMvc.perform(get("/open/articles"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.content[0].id").value("abc-123"))
                                .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        void should_returnBadRequest_when_invalidLanguage() throws Exception {
                // When & Then
                mockMvc.perform(get("/open/articles?lang=INVALID_LANG"))
                                .andExpect(status().isOk()) // Assuming ApiResponse structure returns 200 via
                                                            // Controller, but internal code is 400
                                .andExpect(jsonPath("$.status.code").value(400))
                                .andExpect(jsonPath("$.status.message").value("Invalid lang"));
        }

        @Test
        void should_returnArticleDetail_when_foundById() throws Exception {
                // Given
                ArticleDetailResponse detailResponse = ArticleDetailResponse.builder()
                                .id("def-456")
                                .title("Detail View")
                                .content("<p>Content</p>")
                                .build();

                when(articleService.getArticleById("def-456")).thenReturn(detailResponse);

                // When & Then
                mockMvc.perform(get("/open/articles/def-456"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.id").value("def-456"))
                                .andExpect(jsonPath("$.data.title").value("Detail View"));
        }

        @Test
        void should_returnNotFound_when_articleIdDoesNotExist() throws Exception {
                // Given
                when(articleService.getArticleById("not-found")).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/open/articles/not-found"))
                                .andExpect(status().isOk()) // The method currently returns
                                                            // ResponseEntity.ok(ApiResponse.error(...)) implicitly
                                .andExpect(jsonPath("$.status.code").value(404))
                                .andExpect(jsonPath("$.status.message").value("Article not found"));
        }

        @Test
        void should_returnFullCoverImage_when_noRangeHeader() throws Exception {
                File coverFile = Files.createTempFile("article-cover-full", ".png").toFile();
                Files.write(coverFile.toPath(), "cover-image".getBytes());
                when(articleService.getCoverImageFileByFilename("cover.png")).thenReturn(coverFile);

                mockMvc.perform(get("/open/articles/cover-images/cover.png"))
                                .andExpect(status().isOk())
                                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                                                "inline; filename=\"cover.png\""))
                                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, 11))
                                .andExpect(content().bytes("cover-image".getBytes()));
        }

        @Test
        void should_returnCoverImageRange_when_rangeHeaderPresent() throws Exception {
                File coverFile = Files.createTempFile("article-cover-range", ".png").toFile();
                Files.write(coverFile.toPath(), "0123456789".getBytes());
                when(articleService.getCoverImageFileByFilename("cover.png")).thenReturn(coverFile);

                mockMvc.perform(get("/open/articles/cover-images/cover.png")
                                .header(HttpHeaders.RANGE, "bytes=3-6"))
                                .andExpect(status().isPartialContent())
                                .andExpect(header().string(HttpHeaders.ACCEPT_RANGES, "bytes"))
                                .andExpect(header().string(HttpHeaders.CONTENT_RANGE, "bytes 3-6/10"))
                                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, 4))
                                .andExpect(content().bytes("3456".getBytes()));
        }
}
