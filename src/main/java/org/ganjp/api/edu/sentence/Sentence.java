package org.ganjp.api.edu.sentence;

import jakarta.persistence.*;
import lombok.*;
import org.ganjp.api.edu.common.EduBaseEntity;

@Entity
@Table(name = "edu_sentence")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sentence extends EduBaseEntity {
    @Id
    @Column(columnDefinition = "char(36)", nullable = false)
    private String id;
    @Column(length = 800, nullable = false)
    private String name;
    @Column(length = 400)
    private String phonetic;
    @Column(name = "phonetic_audio_filename", length = 60)
    private String phoneticAudioFilename;
    @Column(length = 400)
    private String translation;
    @Column(length = 1000)
    private String explanation;
    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel;
    @Column(length = 20, nullable = false)
    private String channel;
    @Column(length = 100)
    private String tags;
    @Enumerated(EnumType.STRING)
    @Column(length = 2, nullable = false)
    private Language lang;
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
    private Integer term;
    private Integer week;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public enum Language { EN, ZH }
}
