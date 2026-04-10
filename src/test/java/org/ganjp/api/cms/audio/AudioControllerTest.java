package org.ganjp.api.cms.audio;

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

@WebMvcTest(AudioController.class)
class AudioControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AudioService audioService;

        @Test
        void should_returnPaginatedAudios_when_getAudios() throws Exception {
                // Given
                AudioResponse audioResponse = AudioResponse.builder()
                                .id("abc-123")
                                .title("Controller Test")
                                .build();

                PaginatedResponse<AudioResponse> paginatedData = PaginatedResponse.of(
                                List.of(audioResponse), 0, 20, 1);

                when(audioService.getAudios(isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt(), anyString(),
                                anyString()))
                                .thenReturn(paginatedData);

                // When & Then
                mockMvc.perform(get("/open/audios"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.content[0].id").value("abc-123"))
                                .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        void should_returnBadRequest_when_invalidLanguage() throws Exception {
                // When & Then
                mockMvc.perform(get("/open/audios?lang=INVALID_LANG"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(400))
                                .andExpect(jsonPath("$.status.message").value("Invalid lang"));
        }

        @Test
        void should_returnAudioDetail_when_foundById() throws Exception {
                // Given
                AudioResponse detailResponse = AudioResponse.builder()
                                .id("def-456")
                                .title("Detail View")
                                .build();

                when(audioService.getAudioById("def-456")).thenReturn(detailResponse);

                // When & Then
                mockMvc.perform(get("/open/audios/def-456"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.id").value("def-456"))
                                .andExpect(jsonPath("$.data.title").value("Detail View"));
        }

        @Test
        void should_returnNotFound_when_audioIdDoesNotExist() throws Exception {
                // Given
                when(audioService.getAudioById("not-found")).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/open/audios/not-found"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(404))
                                .andExpect(jsonPath("$.status.message").value("Audio not found"));
        }

        @Test
        void should_return404_when_servingMissingAudioFile() throws Exception {
                // Given
                when(audioService.getAudioFile("ghost.mp3")).thenThrow(new IllegalArgumentException("Not found"));

                // When & Then
                mockMvc.perform(get("/open/audios/view/ghost.mp3"))
                                .andExpect(status().isNotFound()); // The exception block maps to 404
        }

        @Test
        void should_return500_when_IOErrorReadingAudioFile() throws Exception {
                // Given
                when(audioService.getAudioFile("corrupted.mp3")).thenThrow(new IOException("Disk error"));

                // When & Then
                mockMvc.perform(get("/open/audios/view/corrupted.mp3"))
                                .andExpect(status().isInternalServerError());
        }

        @org.junit.jupiter.api.io.TempDir
        java.nio.file.Path tempDir;

        @Test
        void should_returnPartialContent_when_rangeHeaderProvided() throws Exception {
                // Given
                java.io.File tempFile = tempDir.resolve("test.mp3").toFile();
                java.nio.file.Files.write(tempFile.toPath(), "0123456789".getBytes());

                when(audioService.getAudioFile("test.mp3")).thenReturn(tempFile);

                // When & Then: Request bytes 0-3 (length 4)
                mockMvc.perform(get("/open/audios/view/test.mp3")
                                .header("Range", "bytes=0-3"))
                                .andExpect(status().isPartialContent())
                                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                                                .string("Content-Range", "bytes 0-3/10"))
                                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                                                .string("0123"));
        }

        @Test
        void should_returnFullContent_when_noRangeHeader() throws Exception {
                // Given
                java.io.File tempFile = tempDir.resolve("full.mp3").toFile();
                java.nio.file.Files.write(tempFile.toPath(), "full_content".getBytes());
                when(audioService.getAudioFile("full.mp3")).thenReturn(tempFile);

                // When & Then
                mockMvc.perform(get("/open/audios/view/full.mp3"))
                                .andExpect(status().isOk())
                                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                                                .string("full_content"));
        }

        @Test
        void should_return500_when_IOErrorReadingCoverImage() throws Exception {
                // Given
                when(audioService.getAudioCoverFile("fail.jpg")).thenThrow(new IOException("Disk failure"));

                // When & Then
                mockMvc.perform(get("/open/audios/cover-images/fail.jpg"))
                                .andExpect(status().isInternalServerError());
        }
}
