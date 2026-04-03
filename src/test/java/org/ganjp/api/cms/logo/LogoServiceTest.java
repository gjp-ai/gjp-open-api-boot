package org.ganjp.api.cms.logo;

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
class LogoServiceTest {

    @Mock
    private LogoRepository logoRepository;

    @Mock
    private LogoUploadProperties uploadProperties;

    @InjectMocks
    private LogoService logoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(logoService, "logoBaseUrl", "http://cms/logos");
    }

    @Test
    void should_returnPaginatedLogos_when_validRequest() {
        // Given
        Logo logo = Logo.builder()
                .name("Test Logo")
                .lang(Logo.Language.EN)
                .filename("logo.png")
                .build();
        logo.setId("abc-123");

        Page<Logo> mockPage = new PageImpl<>(List.of(logo));

        when(logoRepository.searchLogos(eq("Test"), eq(Logo.Language.EN), any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        PaginatedResponse<LogoResponse> response = logoService.getLogos("Test", Logo.Language.EN, null, null, 0, 10, "displayOrder", "asc");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        
        LogoResponse dto = response.getContent().get(0);
        assertThat(dto.getId()).isEqualTo("abc-123");
        assertThat(dto.getName()).isEqualTo("Test Logo");
        assertThat(dto.getUrl()).isEqualTo("http://cms/logos/logo.png");
    }

    @Test
    void should_returnLogo_when_foundById() {
        // Given
        Logo logo = Logo.builder().name("Single Logo").lang(Logo.Language.EN).build();
        logo.setId("def-456");

        when(logoRepository.findById("def-456")).thenReturn(Optional.of(logo));

        // When
        LogoResponse response = logoService.getLogoById("def-456");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("def-456");
        assertThat(response.getName()).isEqualTo("Single Logo");
    }

    @Test
    void should_returnNull_when_logoNotFoundById() {
        // Given
        when(logoRepository.findById("invalid")).thenReturn(Optional.empty());

        // When
        LogoResponse response = logoService.getLogoById("invalid");

        // Then
        assertThat(response).isNull();
    }

    @Test
    void should_throwException_when_logoNotInDatabase() {
        // Given
        when(logoRepository.findByFilenameAndIsActiveTrue("ghost.png")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> logoService.getLogoFile("ghost.png"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Logo not found or not active: ghost.png");
    }

    @Test
    void should_throwException_when_logoFileNotInFileSystem() {
        // Given
        Logo logo = Logo.builder().filename("missing.png").build();
        when(logoRepository.findByFilenameAndIsActiveTrue("missing.png")).thenReturn(Optional.of(logo));
        when(uploadProperties.getDirectory()).thenReturn("/tmp/invalid-dir");

        // When & Then
        assertThatThrownBy(() -> logoService.getLogoFile("missing.png"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Logo file not found: missing.png");
    }
}
