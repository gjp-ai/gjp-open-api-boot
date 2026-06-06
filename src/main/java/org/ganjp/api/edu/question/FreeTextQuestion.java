package org.ganjp.api.edu.question;

import jakarta.persistence.*;
import lombok.*;
import org.ganjp.api.edu.common.EduBaseEntity;

@Entity
@Table(name = "edu_free_text_question")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FreeTextQuestion extends EduBaseEntity {
    @Id @Column(columnDefinition = "char(36)", nullable = false) private String id;
    @Column(length = 500, nullable = false) private String question;
    @Column(length = 500) private String answer;
    @Column(length = 500) private String description;
    @Column(name = "question_a", length = 500) private String questionA;
    @Column(name = "answer_a", length = 500) private String answerA;
    @Column(name = "question_b", length = 500) private String questionB;
    @Column(name = "answer_b", length = 500) private String answerB;
    @Column(name = "question_c", length = 500) private String questionC;
    @Column(name = "answer_c", length = 500) private String answerC;
    @Column(name = "question_d", length = 500) private String questionD;
    @Column(name = "answer_d", length = 500) private String answerD;
    @Column(name = "question_e", length = 500) private String questionE;
    @Column(name = "answer_e", length = 500) private String answerE;
    @Column(name = "question_f", length = 500) private String questionF;
    @Column(name = "answer_f", length = 500) private String answerF;
    @Column(length = 800) private String explanation;
    @Column(name = "difficulty_level", length = 20) private String difficultyLevel;
    @Column(name = "fail_count", nullable = false) private Integer failCount;
    @Column(name = "success_count", nullable = false) private Integer successCount;
    @Column(name = "grade_level", length = 20) private String gradeLevel;
    @Column(length = 20) private String subject;
    @Column(length = 100) private String topic;
    private Integer term;
    private Integer week;
    @Column(length = 20, nullable = false) private String channel;
    @Column(length = 100) private String tags;
    @Enumerated(EnumType.STRING) @Column(length = 2, nullable = false) private Language lang;
    @Column(name = "display_order", nullable = false) private Integer displayOrder;
    @Column(name = "is_active", nullable = false) private Boolean isActive;
    public enum Language { EN, ZH }
}
