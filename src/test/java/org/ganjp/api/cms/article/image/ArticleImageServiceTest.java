package org.ganjp.api.cms.article.image;

import org.ganjp.api.cms.article.ArticleProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleImageServiceTest {

    @Mock
    private ArticleProperties articleProperties;

    @Mock
    private ArticleProperties.ContentImage contentImage;

    @Mock
    private ArticleProperties.Upload upload;

    @InjectMocks
    private ArticleImageService articleImageService;

    @Test
    void should_returnFile_when_getImageFile() {
        // Given
        when(articleProperties.getContentImage()).thenReturn(contentImage);
        when(contentImage.getUpload()).thenReturn(upload);
        when(upload.getDirectory()).thenReturn("/tmp/images");

        // When
        File result = articleImageService.getImageFile("test.png");

        // Then
        assertThat(result.getPath()).contains("test.png");
        assertThat(result.getPath()).contains("/tmp/images");
    }
}
