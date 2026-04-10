package org.ganjp.api.cms.question;

import org.ganjp.api.core.model.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
class QuestionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private QuestionService questionService;

        @Test
        void should_returnPaginatedQuestions_when_getQuestions() throws Exception {
                // Given
                QuestionResponse questionResponse = QuestionResponse.builder()
                                .id("abc-123")
                                .question("Controller Test")
                                .build();

                PaginatedResponse<QuestionResponse> paginatedData = PaginatedResponse.of(
                                List.of(questionResponse), 0, 20, 1);

                when(questionService.getQuestions(isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt(),
                                anyString(), anyString()))
                                .thenReturn(paginatedData);

                // When & Then
                mockMvc.perform(get("/open/questions"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.content[0].id").value("abc-123"))
                                .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        void should_returnBadRequest_when_invalidLanguage() throws Exception {
                // When & Then
                mockMvc.perform(get("/open/questions?lang=INVALID_LANG"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(400))
                                .andExpect(jsonPath("$.status.message").value("Invalid lang"));
        }

        @Test
        void should_returnQuestionDetail_when_foundById() throws Exception {
                // Given
                QuestionResponse detailResponse = QuestionResponse.builder()
                                .id("def-456")
                                .question("Detail View")
                                .build();

                when(questionService.getQuestionById("def-456")).thenReturn(detailResponse);

                // When & Then
                mockMvc.perform(get("/open/questions/def-456"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.id").value("def-456"))
                                .andExpect(jsonPath("$.data.question").value("Detail View"));
        }

        @Test
        void should_returnNotFound_when_questionIdDoesNotExist() throws Exception {
                // Given
                when(questionService.getQuestionById("not-found")).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/open/questions/not-found"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(404))
                                .andExpect(jsonPath("$.status.message").value("Question not found"));
        }
}
