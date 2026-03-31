package org.ganjp.api.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginatedResponseTest {

    @Test
    void should_createPaginatedResponse_withCorrectMetadata() {
        // Given
        List<String> items = List.of("a", "b", "c");
        
        // When
        PaginatedResponse<String> response = PaginatedResponse.of(items, 1, 20, 100);

        // Then
        assertThat(response.getContent()).hasSize(3);
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getSize()).isEqualTo(20);
        assertThat(response.getTotalElements()).isEqualTo(100);
        assertThat(response.getTotalPages()).isEqualTo(5); // 100 / 20
    }

    @Test
    void should_calculateTotalPages_when_itemsMissingOne() {
        // Given
        List<String> items = List.of("a");
        
        // When
        PaginatedResponse<String> response = PaginatedResponse.of(items, 0, 20, 21);

        // Then
        assertThat(response.getTotalPages()).isEqualTo(2);
    }

    @Test
    void should_returnZeroPages_when_totalElementsZero() {
        // When
        PaginatedResponse<String> response = PaginatedResponse.of(List.of(), 0, 20, 0);

        // Then
        assertThat(response.getTotalPages()).isZero();
    }

    @Test
    void should_returnZeroPages_when_sizeIsZero() {
        // When
        PaginatedResponse<String> response = PaginatedResponse.of(List.of("a"), 0, 0, 1);

        // Then
        assertThat(response.getTotalPages()).isZero();
    }

    @Test
    void should_createFromSpringPage() {
        // Given
        org.springframework.data.domain.Page<String> springPage = new org.springframework.data.domain.PageImpl<>(
            List.of("a", "b"), 
            org.springframework.data.domain.PageRequest.of(1, 10), 
            100
        );

        // When
        PaginatedResponse<String> response = PaginatedResponse.of(springPage);

        // Then
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(100);
        assertThat(response.getTotalPages()).isEqualTo(10);
    }

    @Test
    void should_supportAllArgsConstructor() {
        PaginatedResponse<String> response = new PaginatedResponse<>(List.of("a"), 0, 10, 1, 1);
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalPages()).isEqualTo(1);
    }
}
