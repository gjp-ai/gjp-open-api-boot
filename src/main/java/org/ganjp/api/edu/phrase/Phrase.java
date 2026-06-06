package org.ganjp.api.edu.phrase;

import jakarta.persistence.*;
import lombok.*;
import org.ganjp.api.edu.common.EduBaseEntity;

@Entity
@Table(name = "edu_phrase")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Phrase extends EduBaseEntity {
    @Id
    @Column(columnDefinition = "char(36)", nullable = false)
    private String id;
    @Column(length = 128, nullable = false)
    private String name;
    @Column(length = 200)
    private String phonetic;
    @Column(name = "phonetic_audio_filename", length = 60)
    private String phoneticAudioFilename;
    @Column(name = "phonetic_audio_original_url", length = 256)
    private String phoneticAudioOriginalUrl;
    @Column(length = 200)
    private String synonyms;
    @Column(length = 200)
    private String translation;
    @Column(name = "meaning_clue", length = 300)
    private String meaningClue;
    @Column(length = 300)
    private String meaning;
    @Column(name = "easy_meaning", length = 128)
    private String easyMeaning;
    @Column(name = "sentence_one", length = 300)
    private String sentenceOne;
    @Column(name = "sentence_two", length = 300)
    private String sentenceTwo;
    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel;
    @Column(name = "dictionary_url", length = 256)
    private String dictionaryUrl;
    private Integer term;
    private Integer week;
    @Column(length = 20, nullable = false)
    private String channel;
    @Column(length = 100)
    private String tags;
    @Enumerated(EnumType.STRING)
    @Column(length = 2, nullable = false)
    private Language lang;
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public enum Language { EN, ZH }
}
