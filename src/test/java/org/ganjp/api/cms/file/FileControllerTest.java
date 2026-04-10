package org.ganjp.api.cms.file;

import org.ganjp.api.core.model.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
class FileControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private FileService fileService;

        @Test
        void should_returnPaginatedFiles_when_getFiles() throws Exception {
                // Given
                FileResponse fileResponse = FileResponse.builder()
                                .id("abc-123")
                                .name("Download Test")
                                .build();

                PaginatedResponse<FileResponse> paginatedData = PaginatedResponse.of(
                                List.of(fileResponse), 0, 20, 1);

                when(fileService.getFiles(isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt(), anyString(),
                                anyString()))
                                .thenReturn(paginatedData);

                // When & Then
                mockMvc.perform(get("/open/files"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.content[0].id").value("abc-123"))
                                .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        void should_returnBadRequest_when_invalidLanguage() throws Exception {
                // When & Then
                mockMvc.perform(get("/open/files?lang=INVALID_LANG"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(400))
                                .andExpect(jsonPath("$.status.message").value("Invalid lang"));
        }

        @Test
        void should_returnFileDetail_when_foundById() throws Exception {
                // Given
                FileResponse detailResponse = FileResponse.builder()
                                .id("def-456")
                                .name("Detail View")
                                .build();

                when(fileService.getFileById("def-456")).thenReturn(detailResponse);

                // When & Then
                mockMvc.perform(get("/open/files/def-456"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.id").value("def-456"))
                                .andExpect(jsonPath("$.data.name").value("Detail View"));
        }

        @Test
        void should_returnNotFound_when_fileIdDoesNotExist() throws Exception {
                // Given
                when(fileService.getFileById("not-found")).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/open/files/not-found"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(404))
                                .andExpect(jsonPath("$.status.message").value("File not found"));
        }

        @Test
        void should_return404_when_servingMissingFile() throws Exception {
                // Given
                when(fileService.getFileResource("ghost.zip")).thenThrow(new IllegalArgumentException("Not found"));

                // When & Then
                mockMvc.perform(get("/open/files/view/ghost.zip"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void should_return500_when_IOErrorReadingFile() throws Exception {
                // Given
                when(fileService.getFileResource("corrupted.zip")).thenThrow(new IOException("Disk error"));

                // When & Then
                mockMvc.perform(get("/open/files/view/corrupted.zip"))
                                .andExpect(status().isInternalServerError());
        }

        @org.junit.jupiter.api.io.TempDir
        java.nio.file.Path tempDir;

        @Test
        void should_returnFile_when_viewFileSuccess() throws Exception {
                // Given
                java.io.File tempFile = tempDir.resolve("success.txt").toFile();
                java.nio.file.Files.write(tempFile.toPath(), "content".getBytes());
                when(fileService.getFileResource("success.txt")).thenReturn(tempFile);

                // When & Then
                mockMvc.perform(get("/open/files/view/success.txt"))
                                .andExpect(status().isOk())
                                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                                                .string("content"))
                                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                                                .string("Content-Disposition", "attachment; filename=\"success.txt\""));
        }
}
