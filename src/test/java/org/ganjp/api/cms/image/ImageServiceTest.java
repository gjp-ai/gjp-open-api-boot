package org.ganjp.api.cms.image;

import org.ganjp.api.core.model.PaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageUploadProperties imageUploadProperties;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "imageBaseUrl", "http://cms/images");
    }

    @Test
    void should_returnPaginatedImages_when_validRequest() {
        // Given
        Image image = Image.builder()
                .name("Test Image")
                .lang(Image.Language.EN)
                .filename("test.jpg")
                .thumbnailFilename("test-thumb.jpg")
                .build();
        image.setId("abc-123");

        Page<Image> mockPage = new PageImpl<>(List.of(image));

        when(imageRepository.searchImages(eq("Test"), eq(Image.Language.EN), any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        PaginatedResponse<ImageResponse> response = imageService.getImages("Test", Image.Language.EN, null, null, 0, 10, "displayOrder", "asc");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        
        ImageResponse dto = response.getContent().get(0);
        assertThat(dto.getId()).isEqualTo("abc-123");
        assertThat(dto.getName()).isEqualTo("Test Image");
        assertThat(dto.getUrl()).isEqualTo("http://cms/images/test.jpg");
        assertThat(dto.getThumbnailUrl()).isEqualTo("http://cms/images/test-thumb.jpg");
    }

    @Test
    void should_returnImage_when_foundById() {
        // Given
        Image image = Image.builder().name("Single Image").lang(Image.Language.EN).build();
        image.setId("def-456");

        when(imageRepository.findByIdAndIsActiveTrue("def-456")).thenReturn(Optional.of(image));

        // When
        ImageResponse response = imageService.getImageById("def-456");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("def-456");
        assertThat(response.getName()).isEqualTo("Single Image");
    }

    @Test
    void should_returnNull_when_imageNotFoundById() {
        // Given
        when(imageRepository.findByIdAndIsActiveTrue("invalid")).thenReturn(Optional.empty());

        // When
        ImageResponse response = imageService.getImageById("invalid");

        // Then
        assertThat(response).isNull();
    }

    @Test
    void should_throwException_when_imageNotInDatabase() {
        // Given
        when(imageRepository.findByFilenameAndIsActiveTrue("ghost.jpg")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> imageService.getImageFile("ghost.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Image not found or not active: ghost.jpg");
    }

    @Test
    void should_throwException_when_imageFileNotInFileSystem() {
        // Given
        Image image = Image.builder().filename("missing.jpg").build();
        when(imageRepository.findByFilenameAndIsActiveTrue("missing.jpg")).thenReturn(Optional.of(image));
        when(imageUploadProperties.getDirectory()).thenReturn("/tmp/invalid-dir");

        // When & Then
        assertThatThrownBy(() -> imageService.getImageFile("missing.jpg"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Image file not found: missing.jpg");
    }
}
