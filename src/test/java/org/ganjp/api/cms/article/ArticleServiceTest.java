package org.ganjp.api.cms.article;

import org.ganjp.api.core.model.PaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ArticleProperties articleProperties;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        // Setup code if needed
    }

    @Test
    void should_returnPaginatedArticles_when_validRequest() {
        // Given
        Article article = Article.builder()
                .title("Test Article")
                .summary("Test Summary")
                .lang(Article.Language.EN)
                .coverImageFilename("cover.jpg")
                .build();
        article.setId("abc-123");

        Page<Article> mockPage = new PageImpl<>(List.of(article));
        

        when(articleRepository.searchArticles(eq("Test"), eq(Article.Language.EN), any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        when(articleProperties.getCoverImage().getBaseUrl()).thenReturn("http://cms/cover");

        // When
        PaginatedResponse<ArticleResponse> response = articleService.getArticles("Test", Article.Language.EN, null, null, null, 0, 10, "displayOrder", "asc");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        
        ArticleResponse dto = response.getContent().get(0);
        assertThat(dto.getId()).isEqualTo("abc-123");
        assertThat(dto.getTitle()).isEqualTo("Test Article");
        assertThat(dto.getCoverImageUrl()).isEqualTo("http://cms/cover/cover.jpg");
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void should_returnArticleDetail_when_foundById() {
        // Given
        Article article = Article.builder()
                .title("Full Article")
                .content("<p>HTML Body</p>")
                .lang(Article.Language.EN)
                .build();
        article.setId("def-456");

        when(articleRepository.findById("def-456")).thenReturn(Optional.of(article));
        when(articleProperties.getCoverImage().getBaseUrl()).thenReturn("http://cms/cover");

        // When
        ArticleDetailResponse response = articleService.getArticleById("def-456");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("def-456");
        assertThat(response.getTitle()).isEqualTo("Full Article");
        assertThat(response.getContent()).isEqualTo("<p>HTML Body</p>");
    }

    @Test
    void should_returnNull_when_articleNotFoundById() {
        // Given
        when(articleRepository.findById("invalid")).thenReturn(Optional.empty());

        // When
        ArticleDetailResponse response = articleService.getArticleById("invalid");

        // Then
        assertThat(response).isNull();
    }

    @Test
    void should_throwException_when_filenameIsNull() {
        // When & Then
        assertThatThrownBy(() -> articleService.getCoverImageFileByFilename(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("filename is null");
    }

    @Test
    void should_throwException_when_coverImageFileNotFound() {
        // Given
        when(articleProperties.getCoverImage().getUpload().getDirectory()).thenReturn("/tmp/invalid-dir");

        // When & Then
        assertThatThrownBy(() -> articleService.getCoverImageFileByFilename("non-existent-file.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cover image file not found");
    }
}
