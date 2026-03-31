package org.ganjp.api.cms.util;

import org.ganjp.api.cms.article.Article;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CmsUtilTest {

    @Test
    void should_parseLanguage_when_validString() {
        // When
        Article.Language lang = CmsUtil.parseLanguage("EN", Article.Language.class);

        // Then
        assertThat(lang).isEqualTo(Article.Language.EN);
    }

    @Test
    void should_returnNull_when_invalidLanguageString() {
        // When
        Article.Language lang = CmsUtil.parseLanguage("INVALID", Article.Language.class);

        // Then
        assertThat(lang).isNull();
    }

    @Test
    void should_returnNull_when_blankLanguageString() {
        // When
        assertThat(CmsUtil.parseLanguage(null, Article.Language.class)).isNull();
        assertThat(CmsUtil.parseLanguage("", Article.Language.class)).isNull();
    }

    @Test
    void should_throwException_when_filenameIsInvalid() {
        // Then
        assertThatThrownBy(() -> CmsUtil.validateFilename("../etc/passwd"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CmsUtil.validateFilename("dir/file.txt"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_notThrow_when_filenameIsValid() {
        // When & Then
        CmsUtil.validateFilename("image.png");
        CmsUtil.validateFilename("document-2024.pdf");
    }

    @Test
    void should_buildPageable_withCorrectSort() {
        // When
        Pageable pageable = CmsUtil.buildPageable(1, 10, "title", "desc");

        // Then
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(10);
        Sort.Order order = pageable.getSort().getOrderFor("title");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void should_joinBaseAndPath_correctly() {
        // Case 1: Base and path both have no slash
        assertThat(CmsUtil.joinBaseAndPath("http://base", "file.png")).isEqualTo("http://base/file.png");
        
        // Case 2: Base ends with slash
        assertThat(CmsUtil.joinBaseAndPath("http://base/", "file.png")).isEqualTo("http://base/file.png");
        
        // Case 3: Path starts with slash
        assertThat(CmsUtil.joinBaseAndPath("http://base", "/file.png")).isEqualTo("http://base/file.png");
        
        // Case 4: Both have slash
        assertThat(CmsUtil.joinBaseAndPath("http://base/", "/file.png")).isEqualTo("http://base/file.png");

        // Edge cases
        assertThat(CmsUtil.joinBaseAndPath(null, "file.png")).isNull();
        assertThat(CmsUtil.joinBaseAndPath("", "file.png")).isNull();
        assertThat(CmsUtil.joinBaseAndPath("http://base", null)).isNull();
        assertThat(CmsUtil.joinBaseAndPath("http://base", "")).isNull();
        assertThat(CmsUtil.joinBaseAndPath(null, "http://external")).isEqualTo("http://external");
        assertThat(CmsUtil.joinBaseAndPath(null, "/absolute")).isEqualTo("/absolute");
        assertThat(CmsUtil.joinBaseAndPath(null, "relative")).isNull();
    }

    @Test
    void should_joinBasePathWithSegment_correctly() {
        // All parts present
        assertThat(CmsUtil.joinBasePathWithSegment("http://base", "seg", "file.png")).isEqualTo("http://base/seg/file.png");
        assertThat(CmsUtil.joinBasePathWithSegment("http://base/", "/seg/", "/file.png")).isEqualTo("http://base/seg/file.png");
        assertThat(CmsUtil.joinBasePathWithSegment("http://base/", "seg", "file.png")).isEqualTo("http://base/seg/file.png");
        
        // Null base
        assertThat(CmsUtil.joinBasePathWithSegment(null, "seg", "file.png")).isEqualTo("/seg/file.png");
        assertThat(CmsUtil.joinBasePathWithSegment(null, "/seg", "/file.png")).isEqualTo("/seg/file.png");
        assertThat(CmsUtil.joinBasePathWithSegment(null, "seg/", "file")).isEqualTo("/seg/file");
        assertThat(CmsUtil.joinBasePathWithSegment(null, null, "/file.png")).isEqualTo("/file.png");
        assertThat(CmsUtil.joinBasePathWithSegment(null, null, "http://ext")).isEqualTo("http://ext");
        
        // Null/Empty path
        assertThat(CmsUtil.joinBasePathWithSegment("http://base", "seg", null)).isNull();
        assertThat(CmsUtil.joinBasePathWithSegment("http://base", "seg", "   ")).isNull();
        
        // Empty base
        assertThat(CmsUtil.joinBasePathWithSegment(" ", "seg", "file")).isEqualTo("/seg/file");
    }

    @Test
    void should_determineContentType_correctly() {
        assertThat(CmsUtil.determineContentType("t.png")).isEqualTo("image/png");
        assertThat(CmsUtil.determineContentType("t.jpg")).isEqualTo("image/jpeg");
        assertThat(CmsUtil.determineContentType("t.jpeg")).isEqualTo("image/jpeg");
        assertThat(CmsUtil.determineContentType("t.gif")).isEqualTo("image/gif");
        assertThat(CmsUtil.determineContentType("t.svg")).isEqualTo("image/svg+xml");
        assertThat(CmsUtil.determineContentType("t.webp")).isEqualTo("image/webp");
        assertThat(CmsUtil.determineContentType("t.bmp")).isEqualTo("image/bmp");
        assertThat(CmsUtil.determineContentType("t.mp4")).isEqualTo("video/mp4");
        assertThat(CmsUtil.determineContentType("t.webm")).isEqualTo("video/webm");
        assertThat(CmsUtil.determineContentType("t.ogv")).isEqualTo("video/ogg");
        assertThat(CmsUtil.determineContentType("t.mov")).isEqualTo("video/quicktime");
        assertThat(CmsUtil.determineContentType("t.mkv")).isEqualTo("video/x-matroska");
        assertThat(CmsUtil.determineContentType("t.mp3")).isEqualTo("audio/mpeg");
        assertThat(CmsUtil.determineContentType("t.wav")).isEqualTo("audio/wav");
        assertThat(CmsUtil.determineContentType("t.flac")).isEqualTo("audio/flac");
        assertThat(CmsUtil.determineContentType("t.aac")).isEqualTo("audio/aac");
        assertThat(CmsUtil.determineContentType("t.m4a")).isEqualTo("audio/mp4");
        assertThat(CmsUtil.determineContentType("t.pdf")).isEqualTo("application/pdf");
        assertThat(CmsUtil.determineContentType("unknown")).isEqualTo("application/octet-stream");
    }
}
