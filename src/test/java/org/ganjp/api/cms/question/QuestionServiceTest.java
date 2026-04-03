package org.ganjp.api.cms.question;

import org.ganjp.api.core.model.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    @Test
    void should_returnPaginatedQuestions_when_validRequest() {
        // Given
        Question question = Question.builder()
                .question("What is Spring?")
                .answer("Framework")
                .lang(Question.Language.EN)
                .build();
        question.setId("abc-123");

        Page<Question> mockPage = new PageImpl<>(List.of(question));

        when(questionRepository.search(eq("Spring"), eq(Question.Language.EN), any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        PaginatedResponse<QuestionResponse> response = questionService.getQuestions("Spring", Question.Language.EN, null, null, 0, 10, "displayOrder", "asc");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        
        QuestionResponse dto = response.getContent().get(0);
        assertThat(dto.getId()).isEqualTo("abc-123");
        assertThat(dto.getQuestion()).isEqualTo("What is Spring?");
        assertThat(dto.getAnswer()).isEqualTo("Framework");
    }

    @Test
    void should_returnQuestion_when_foundById() {
        // Given
        Question question = Question.builder().question("Why Java?").lang(Question.Language.EN).build();
        question.setId("def-456");

        when(questionRepository.findById("def-456")).thenReturn(Optional.of(question));

        // When
        QuestionResponse response = questionService.getQuestionById("def-456");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("def-456");
        assertThat(response.getQuestion()).isEqualTo("Why Java?");
    }

    @Test
    void should_returnNull_when_questionNotFoundById() {
        // Given
        when(questionRepository.findById("invalid")).thenReturn(Optional.empty());

        // When
        QuestionResponse response = questionService.getQuestionById("invalid");

        // Then
        assertThat(response).isNull();
    }
}
