package org.ganjp.api.cms.image;

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

@WebMvcTest(ImageController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @Test
    void should_returnPaginatedImages_when_getImages() throws Exception {
        // Given
        ImageResponse imageResponse = ImageResponse.builder()
                .id("abc-123")
                .name("Controller Test")
                .build();

        PaginatedResponse<ImageResponse> paginatedData = PaginatedResponse.of(
                List.of(imageResponse), 0, 20, 1
        );

        when(imageService.getImages(isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(paginatedData);

        // When & Then
        mockMvc.perform(get("/v1/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(200))
                .andExpect(jsonPath("$.data.content[0].id").value("abc-123"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void should_returnBadRequest_when_invalidLanguage() throws Exception {
        // When & Then
        mockMvc.perform(get("/v1/images?lang=INVALID_LANG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(400))
                .andExpect(jsonPath("$.status.message").value("Invalid lang"));
    }

    @Test
    void should_returnImageDetail_when_foundById() throws Exception {
        // Given
        ImageResponse detailResponse = ImageResponse.builder()
                .id("def-456")
                .name("Detail View")
                .build();

        when(imageService.getImageById("def-456")).thenReturn(detailResponse);

        // When & Then
        mockMvc.perform(get("/v1/images/def-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(200))
                .andExpect(jsonPath("$.data.id").value("def-456"))
                .andExpect(jsonPath("$.data.name").value("Detail View"));
    }

    @Test
    void should_returnNotFound_when_imageIdDoesNotExist() throws Exception {
        // Given
        when(imageService.getImageById("not-found")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/v1/images/not-found"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(404))
                .andExpect(jsonPath("$.status.message").value("Image not found"));
    }

    @Test
    void should_return404_when_servingMissingImageFile() throws Exception {
        // Given
        when(imageService.getImageFile("ghost.jpg")).thenThrow(new IllegalArgumentException("Not found"));

        // When & Then
        mockMvc.perform(get("/v1/images/view/ghost.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return500_when_IOErrorReadingImageFile() throws Exception {
        // Given
        when(imageService.getImageFile("corrupted.jpg")).thenThrow(new IOException("Disk error"));

        // When & Then
        mockMvc.perform(get("/v1/images/view/corrupted.jpg"))
                .andExpect(status().isInternalServerError());
    }
    @org.junit.jupiter.api.io.TempDir
    java.nio.file.Path tempDir;

    @Test
    void should_returnImage_when_viewImageSuccess() throws Exception {
        // Given
        java.io.File tempFile = tempDir.resolve("success.png").toFile();
        java.nio.file.Files.write(tempFile.toPath(), "content".getBytes());
        when(imageService.getImageFile("success.png")).thenReturn(tempFile);

        // When & Then
        mockMvc.perform(get("/v1/images/view/success.png"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string("content"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Content-Disposition", "inline; filename=\"success.png\""));
    }
}
