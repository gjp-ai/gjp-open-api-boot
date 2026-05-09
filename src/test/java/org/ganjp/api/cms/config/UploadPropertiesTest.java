package org.ganjp.api.cms.config;

import org.ganjp.api.cms.article.ArticleProperties;
import org.ganjp.api.cms.audio.AudioUploadProperties;
import org.ganjp.api.cms.file.FileUploadProperties;
import org.ganjp.api.cms.image.ImageUploadProperties;
import org.ganjp.api.cms.logo.LogoUploadProperties;
import org.ganjp.api.cms.video.VideoUploadProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UploadPropertiesTest {

    @Test
    void should_storeDirectory_when_setOnSimpleUploadProperties() {
        AudioUploadProperties audio = new AudioUploadProperties();
        FileUploadProperties file = new FileUploadProperties();
        ImageUploadProperties image = new ImageUploadProperties();
        VideoUploadProperties video = new VideoUploadProperties();

        audio.setDirectory("/tmp/audio");
        file.setDirectory("/tmp/files");
        image.setDirectory("/tmp/images");
        video.setDirectory("/tmp/videos");

        assertThat(audio.getDirectory()).isEqualTo("/tmp/audio");
        assertThat(file.getDirectory()).isEqualTo("/tmp/files");
        assertThat(image.getDirectory()).isEqualTo("/tmp/images");
        assertThat(video.getDirectory()).isEqualTo("/tmp/videos");
    }

    @Test
    void should_useDefaultLogoDirectory_and_allowOverride() {
        LogoUploadProperties properties = new LogoUploadProperties();

        assertThat(properties.getDirectory()).isEqualTo("uploads/logos");

        properties.setDirectory("/tmp/logos");

        assertThat(properties.getDirectory()).isEqualTo("/tmp/logos");
    }

    @Test
    void should_storeNestedArticleImageProperties() {
        ArticleProperties properties = new ArticleProperties();
        ArticleProperties.CoverImage coverImage = new ArticleProperties.CoverImage();
        ArticleProperties.ContentImage contentImage = new ArticleProperties.ContentImage();
        ArticleProperties.Upload coverUpload = new ArticleProperties.Upload();
        ArticleProperties.Upload contentUpload = new ArticleProperties.Upload();

        coverUpload.setDirectory("/tmp/article-covers");
        contentUpload.setDirectory("/tmp/article-content");
        coverImage.setBaseUrl("https://cdn.example.com/covers");
        coverImage.setUpload(coverUpload);
        contentImage.setBaseUrl("https://cdn.example.com/content");
        contentImage.setUpload(contentUpload);
        properties.setCoverImage(coverImage);
        properties.setContentImage(contentImage);

        assertThat(properties.getCoverImage().getBaseUrl()).isEqualTo("https://cdn.example.com/covers");
        assertThat(properties.getCoverImage().getUpload().getDirectory()).isEqualTo("/tmp/article-covers");
        assertThat(properties.getContentImage().getBaseUrl()).isEqualTo("https://cdn.example.com/content");
        assertThat(properties.getContentImage().getUpload().getDirectory()).isEqualTo("/tmp/article-content");
    }
}
