package org.ganjp.api.cms.video;

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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoUploadProperties uploadProperties;

    @InjectMocks
    private VideoService videoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(videoService, "videoBaseUrl", "http://cms/videos");
        ReflectionTestUtils.setField(videoService, "videoCoverImageBaseUrl", "http://cms/videos/covers");
    }

    @Test
    void should_returnPaginatedVideos_when_validRequest() {
        Video video = Video.builder()
                .name("Test Video")
                .description("Desc")
                .lang(Video.Language.EN)
                .filename("test.mp4")
                .coverImageFilename("cover.jpg")
                .build();
        video.setId("abc-123");

        Page<Video> mockPage = new PageImpl<>(List.of(video));

        when(videoRepository.searchVideos(eq("Test"), eq(Video.Language.EN), any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        PaginatedResponse<VideoResponse> response = videoService.getVideos("Test", Video.Language.EN, null, null, 0, 10, "displayOrder", "asc");

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        
        VideoResponse dto = response.getContent().get(0);
        assertThat(dto.getId()).isEqualTo("abc-123");
        assertThat(dto.getTitle()).isEqualTo("Test Video");
        assertThat(dto.getUrl()).isEqualTo("http://cms/videos/test.mp4");
        assertThat(dto.getCoverImageUrl()).isEqualTo("http://cms/videos/covers/cover.jpg");
    }

    @Test
    void should_returnVideo_when_foundById() {
        Video video = Video.builder().name("Single Video").lang(Video.Language.EN).build();
        video.setId("def-456");

        when(videoRepository.findById("def-456")).thenReturn(Optional.of(video));

        VideoResponse response = videoService.getVideoById("def-456");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("def-456");
        assertThat(response.getTitle()).isEqualTo("Single Video");
    }

    @Test
    void should_returnNull_when_videoNotFoundById() {
        when(videoRepository.findById("invalid")).thenReturn(Optional.empty());
        VideoResponse response = videoService.getVideoById("invalid");
        assertThat(response).isNull();
    }

    @Test
    void should_throwException_when_videoFileNull() {
        assertThatThrownBy(() -> videoService.getVideoFileByFilename(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("filename is null");
    }

    @Test
    void should_throwException_when_videoFileNotInFileSystem() {
        when(uploadProperties.getDirectory()).thenReturn("/tmp/invalid-dir");
        assertThatThrownBy(() -> videoService.getVideoFileByFilename("missing.mp4"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Video file not found: missing.mp4");
    }

    @Test
    void should_throwException_when_coverFileNull() {
        assertThatThrownBy(() -> videoService.getCoverImageFileByFilename(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("filename is null");
    }

    @Test
    void should_throwException_when_coverFileNotFoundSystem() {
        when(uploadProperties.getDirectory()).thenReturn("/tmp/invalid-dir");
        assertThatThrownBy(() -> videoService.getCoverImageFileByFilename("missing-cover.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cover image file not found: missing-cover.jpg");
    }
}
