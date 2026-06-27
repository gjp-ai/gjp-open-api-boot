package org.ganjp.api.edu.vocabulary;

import jakarta.persistence.*;
import lombok.*;
import org.ganjp.api.edu.common.EduBaseEntity;

@Entity
@Table(name = "edu_vocabulary")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vocabulary extends EduBaseEntity {
    @Id
    @Column(columnDefinition = "char(36)", nullable = false)
    private String id;
    @Column(length = 50, nullable = false)
    private String name;
    @Column(name = "phonetic_us", length = 100)
    private String phoneticUs;
    @Column(name = "phonetic_us_audio_filename", length = 60)
    private String phoneticUsAudioFilename;
    @Column(name = "phonetic_us_audio_original_url", length = 256)
    private String phoneticUsAudioOriginalUrl;
    @Column(name = "phonetic_uk", length = 100)
    private String phoneticUk;
    @Column(name = "phonetic_uk_audio_filename", length = 60)
    private String phoneticUkAudioFilename;
    @Column(name = "phonetic_uk_audio_original_url", length = 256)
    private String phoneticUkAudioOriginalUrl;
    @Column(name = "part_of_speech", length = 20)
    private String partOfSpeech;
    @Column(length = 60)
    private String synonyms;
    @Column(length = 60)
    private String translation;
    @Column(name = "meaning_clue", length = 200)
    private String meaningClue;
    @Column(length = 200)
    private String meaning;
    @Column(name = "easy_meaning", length = 200)
    private String easyMeaning;
    @Column(name = "sentence_one", length = 200)
    private String sentenceOne;
    @Column(name = "sentence_two", length = 200)
    private String sentenceTwo;
    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel;
    @Column(name = "dictionary_url", length = 256)
    private String dictionaryUrl;
    @Column(name = "additional_info", length = 1000)
    private String additionalInfo;
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
