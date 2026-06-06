package org.ganjp.api.edu.questionimage;

import jakarta.persistence.*;
import lombok.*;
import org.ganjp.api.edu.common.EduBaseEntity;

@Entity
@Table(name = "edu_question_image")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuestionImage extends EduBaseEntity {
    @Id
    @Column(columnDefinition = "char(36)", nullable = false)
    private String id;
    @Column(name = "multiple_choice_question_id", columnDefinition = "char(36)")
    private String multipleChoiceQuestionId;
    @Column(name = "free_text_question_id", columnDefinition = "char(36)")
    private String freeTextQuestionId;
    @Column(name = "true_false_question_id", columnDefinition = "char(36)")
    private String trueFalseQuestionId;
    @Column(name = "fill_blank_question_id", columnDefinition = "char(36)")
    private String fillBlankQuestionId;
    @Column(length = 60, nullable = false)
    private String filename;
    @Column(name = "original_url", length = 256)
    private String originalUrl;
    private Integer width;
    private Integer height;
    @Enumerated(EnumType.STRING)
    @Column(length = 2, nullable = false)
    private Language lang;
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public enum Language { EN, ZH }
}
