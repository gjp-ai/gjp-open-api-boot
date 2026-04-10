package org.ganjp.api.cms.logo;

import org.ganjp.api.core.model.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogoController.class)
class LogoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private LogoService logoService;

        @Test
        void should_returnPaginatedLogos_when_getLogos() throws Exception {
                // Given
                LogoResponse logoResponse = LogoResponse.builder()
                                .id("abc-123")
                                .name("Logo Test")
                                .build();

                PaginatedResponse<LogoResponse> paginatedData = PaginatedResponse.of(
                                List.of(logoResponse), 0, 20, 1);

                when(logoService.getLogos(isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt(), anyString(),
                                anyString()))
                                .thenReturn(paginatedData);

                // When & Then
                mockMvc.perform(get("/open/logos"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.content[0].id").value("abc-123"))
                                .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        void should_returnBadRequest_when_invalidLanguage() throws Exception {
                // When & Then
                mockMvc.perform(get("/open/logos?lang=INVALID_LANG"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(400))
                                .andExpect(jsonPath("$.status.message").value("Invalid lang"));
        }

        @Test
        void should_returnLogoDetail_when_foundById() throws Exception {
                // Given
                LogoResponse detailResponse = LogoResponse.builder()
                                .id("def-456")
                                .name("Detail View")
                                .build();

                when(logoService.getLogoById("def-456")).thenReturn(detailResponse);

                // When & Then
                mockMvc.perform(get("/open/logos/def-456"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.id").value("def-456"))
                                .andExpect(jsonPath("$.data.name").value("Detail View"));
        }

        @Test
        void should_returnNotFound_when_logoIdDoesNotExist() throws Exception {
                // Given
                when(logoService.getLogoById("not-found")).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/open/logos/not-found"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(404))
                                .andExpect(jsonPath("$.status.message").value("Logo not found"));
        }

        @Test
        void should_return404_when_servingMissingLogoFile() throws Exception {
                // Given
                when(logoService.getLogoFile("ghost.png")).thenThrow(new IllegalArgumentException("Not found"));

                // When & Then
                mockMvc.perform(get("/open/logos/view/ghost.png"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void should_return500_when_IOErrorReadingLogoFile() throws Exception {
                // Given
                when(logoService.getLogoFile("corrupted.png")).thenThrow(new IOException("Disk error"));

                // When & Then
                mockMvc.perform(get("/open/logos/view/corrupted.png"))
                                .andExpect(status().isInternalServerError());
        }

        @org.junit.jupiter.api.io.TempDir
        java.nio.file.Path tempDir;

        @Test
        void should_returnLogo_when_viewLogoSuccess() throws Exception {
                // Given
                java.io.File tempFile = tempDir.resolve("success.png").toFile();
                java.nio.file.Files.write(tempFile.toPath(), "content".getBytes());
                when(logoService.getLogoFile("success.png")).thenReturn(tempFile);

                // When & Then
                mockMvc.perform(get("/open/logos/view/success.png"))
                                .andExpect(status().isOk())
                                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                                                .string("content"))
                                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                                                .string("Content-Disposition", "inline; filename=\"success.png\""));
        }
}
