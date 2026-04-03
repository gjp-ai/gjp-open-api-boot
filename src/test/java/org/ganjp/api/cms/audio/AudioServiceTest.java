package org.ganjp.api.cms.audio;

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
class AudioServiceTest {

    @Mock
    private AudioRepository audioRepository;

    @Mock
    private AudioUploadProperties uploadProperties;

    @InjectMocks
    private AudioService audioService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(audioService, "audioBaseUrl", "http://cms/audios");
        ReflectionTestUtils.setField(audioService, "audioCoverImageBaseUrl", "http://cms/audios/covers");
    }

    @Test
    void should_returnPaginatedAudios_when_validRequest() {
        // Given
        Audio audio = Audio.builder()
                .name("Podcast 1")
                .description("Desc")
                .lang(Audio.Language.EN)
                .filename("pod1.mp3")
                .coverImageFilename("cover1.jpg")
                .build();
        audio.setId("abc-123");

        Page<Audio> mockPage = new PageImpl<>(List.of(audio));

        when(audioRepository.searchAudios(eq("Podcast"), eq(Audio.Language.EN), any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        PaginatedResponse<AudioResponse> response = audioService.getAudios("Podcast", Audio.Language.EN, null, null, 0, 10, "displayOrder", "asc");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);

        AudioResponse dto = response.getContent().get(0);
        assertThat(dto.getId()).isEqualTo("abc-123");
        assertThat(dto.getTitle()).isEqualTo("Podcast 1");
        assertThat(dto.getUrl()).isEqualTo("http://cms/audios/pod1.mp3");
        assertThat(dto.getCoverImageUrl()).isEqualTo("http://cms/audios/covers/cover1.jpg");
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void should_returnAudio_when_foundById() {
        // Given
        Audio audio = Audio.builder()
                .name("Single Track")
                .lang(Audio.Language.EN)
                .build();
        audio.setId("def-456");

        when(audioRepository.findById("def-456")).thenReturn(Optional.of(audio));

        // When
        AudioResponse response = audioService.getAudioById("def-456");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("def-456");
        assertThat(response.getTitle()).isEqualTo("Single Track");
    }

    @Test
    void should_returnNull_when_audioNotFoundById() {
        // Given
        when(audioRepository.findById("invalid")).thenReturn(Optional.empty());

        // When
        AudioResponse response = audioService.getAudioById("invalid");

        // Then
        assertThat(response).isNull();
    }

    @Test
    void should_throwException_when_audioFileNotInDatabase() {
        // Given
        when(audioRepository.existsByFilename("ghost.mp3")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> audioService.getAudioFile("ghost.mp3"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Audio not found: ghost.mp3");
    }

    @Test
    void should_throwException_when_audioFileNotInFileSystem() {
        // Given
        when(audioRepository.existsByFilename("missing.mp3")).thenReturn(true);
        when(uploadProperties.getDirectory()).thenReturn("/tmp/invalid-dir");

        // When & Then
        assertThatThrownBy(() -> audioService.getAudioFile("missing.mp3"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Audio file not found: missing.mp3");
    }

    @Test
    void should_throwException_when_coverFileNotFoundSystem() {
        // Given
        when(uploadProperties.getDirectory()).thenReturn("/tmp/invalid-dir");

        // When & Then
        assertThatThrownBy(() -> audioService.getAudioCoverFile("missing-cover.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cover image file not found: missing-cover.jpg");
    }
}
