package org.ganjp.api.core.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void should_createSuccess_withDataAndMessage() {
        // Given
        String data = "test data";
        
        // When
        ApiResponse<String> response = ApiResponse.success(data, "Success Message");

        // Then
        assertThat(response.getData()).isEqualTo(data);
        assertThat(response.getStatus().getCode()).isEqualTo(200);
        assertThat(response.getStatus().getMessage()).isEqualTo("Success Message");
    }

    @Test
    void should_createError_withCodeAndMessage() {
        // When
        ApiResponse<Object> response = ApiResponse.error(404, "Not Found", null);

        // Then
        assertThat(response.getData()).isNull();
        assertThat(response.getStatus().getCode()).isEqualTo(404);
        assertThat(response.getStatus().getMessage()).isEqualTo("Not Found");
    }

    @Test
    void should_createError_withData() {
        // Given
        String errorInfo = "Some detail";
        
        // When
        ApiResponse<Object> response = ApiResponse.error(400, "Bad Request", errorInfo);

        // Then
        assertThat(response.getStatus().getErrors()).isEqualTo(errorInfo);
        assertThat(response.getStatus().getCode()).isEqualTo(400);
    }
}
