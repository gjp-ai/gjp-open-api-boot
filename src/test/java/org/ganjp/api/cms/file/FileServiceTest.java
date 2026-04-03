package org.ganjp.api.cms.file;

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
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileUploadProperties uploadProperties;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "fileBaseUrl", "http://cms/files");
    }

    @Test
    void should_returnPaginatedFiles_when_validRequest() {
        // Given
        org.ganjp.api.cms.file.File fileEntity = org.ganjp.api.cms.file.File.builder()
                .name("Download Doc")
                .lang(org.ganjp.api.cms.file.File.Language.EN)
                .filename("doc.pdf")
                .build();
        fileEntity.setId("abc-123");

        Page<org.ganjp.api.cms.file.File> mockPage = new PageImpl<>(List.of(fileEntity));

        when(fileRepository.searchFiles(eq("Doc"), eq(org.ganjp.api.cms.file.File.Language.EN), any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        PaginatedResponse<FileResponse> response = fileService.getFiles("Doc", org.ganjp.api.cms.file.File.Language.EN, null, null, 0, 10, "displayOrder", "asc");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        
        FileResponse dto = response.getContent().get(0);
        assertThat(dto.getId()).isEqualTo("abc-123");
        assertThat(dto.getName()).isEqualTo("Download Doc");
        assertThat(dto.getUrl()).isEqualTo("http://cms/files/doc.pdf");
    }

    @Test
    void should_returnFile_when_foundById() {
        // Given
        org.ganjp.api.cms.file.File fileEntity = org.ganjp.api.cms.file.File.builder().name("Report").lang(org.ganjp.api.cms.file.File.Language.EN).build();
        fileEntity.setId("def-456");

        when(fileRepository.findById("def-456")).thenReturn(Optional.of(fileEntity));

        // When
        FileResponse response = fileService.getFileById("def-456");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("def-456");
        assertThat(response.getName()).isEqualTo("Report");
    }

    @Test
    void should_returnNull_when_fileNotFoundById() {
        // Given
        when(fileRepository.findById("invalid")).thenReturn(Optional.empty());

        // When
        FileResponse response = fileService.getFileById("invalid");

        // Then
        assertThat(response).isNull();
    }

    @Test
    void should_throwException_when_fileNotInDatabase() {
        // Given
        when(fileRepository.existsByFilename("ghost.zip")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> fileService.getFileResource("ghost.zip"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File not found: ghost.zip");
    }

    @Test
    void should_throwException_when_fileNotInFileSystem() {
        // Given
        when(fileRepository.existsByFilename("missing.zip")).thenReturn(true);
        when(uploadProperties.getDirectory()).thenReturn("/tmp/invalid-dir");

        // When & Then
        assertThatThrownBy(() -> fileService.getFileResource("missing.zip"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File not found: missing.zip");
    }
}
