package org.ganjp.api.edu.question;

import jakarta.persistence.*;
import lombok.*;
import org.ganjp.api.edu.common.EduBaseEntity;

@Entity
@Table(name = "edu_multiple_choice_question")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceQuestion extends EduBaseEntity {
    @Id @Column(columnDefinition = "char(36)", nullable = false) private String id;
    @Column(length = 500, nullable = false) private String question;
    @Column(name = "option_a", length = 200) private String optionA;
    @Column(name = "option_b", length = 200) private String optionB;
    @Column(name = "option_c", length = 200) private String optionC;
    @Column(name = "option_d", length = 200) private String optionD;
    @Column(length = 10, nullable = false) private String answer;
    @Column(length = 800) private String explanation;
    @Column(name = "difficulty_level", length = 20) private String difficultyLevel;
    @Column(name = "grade_level", length = 20) private String gradeLevel;
    @Column(length = 20) private String subject;
    @Column(length = 100) private String topic;
    private Integer term;
    private Integer week;
    @Column(name = "fail_count", nullable = false) private Integer failCount;
    @Column(name = "success_count", nullable = false) private Integer successCount;
    @Column(length = 20, nullable = false) private String channel;
    @Column(length = 100) private String tags;
    @Enumerated(EnumType.STRING) @Column(length = 2, nullable = false) private Language lang;
    @Column(name = "display_order", nullable = false) private Integer displayOrder;
    @Column(name = "is_active", nullable = false) private Boolean isActive;
    public enum Language { EN, ZH }
}
