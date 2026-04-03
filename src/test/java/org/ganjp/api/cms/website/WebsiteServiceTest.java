package org.ganjp.api.cms.website;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebsiteServiceTest {

    @Mock
    private WebsiteRepository websiteRepository;

    @InjectMocks
    private WebsiteService websiteService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(websiteService, "logoBaseUrl", "http://cms/logos");
    }

    @Test
    void should_returnPaginatedWebsites_when_validRequest() {
        // Given
        Website website = Website.builder()
                .name("Google")
                .url("https://google.com")
                .logoUrl("google-logo.png")
                .lang(Website.Language.EN)
                .build();
        website.setId("abc-123");

        Page<Website> mockPage = new PageImpl<>(List.of(website));

        when(websiteRepository.searchWebsites(eq("Google"), eq(Website.Language.EN), any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        PaginatedResponse<WebsiteResponse> response = websiteService.getWebsites("Google", Website.Language.EN, null, null, 0, 10, "displayOrder", "asc");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        
        WebsiteResponse dto = response.getContent().get(0);
        assertThat(dto.getId()).isEqualTo("abc-123");
        assertThat(dto.getName()).isEqualTo("Google");
        assertThat(dto.getLogoUrl()).isEqualTo("http://cms/logos/google-logo.png");
    }

    @Test
    void should_returnWebsite_when_foundById() {
        // Given
        Website website = Website.builder().name("Bing").lang(Website.Language.EN).build();
        website.setId("def-456");

        when(websiteRepository.findById("def-456")).thenReturn(Optional.of(website));

        // When
        WebsiteResponse response = websiteService.getWebsiteById("def-456");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("def-456");
        assertThat(response.getName()).isEqualTo("Bing");
    }

    @Test
    void should_returnNull_when_websiteNotFoundById() {
        // Given
        when(websiteRepository.findById("invalid")).thenReturn(Optional.empty());

        // When
        WebsiteResponse response = websiteService.getWebsiteById("invalid");

        // Then
        assertThat(response).isNull();
    }
}
