package org.ganjp.api.edu.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EduLearningItemResponse {
    private String id;
    private String name;
    private String phonetic;
    private String phoneticUs;
    private String phoneticUk;
    private String phoneticAudioFilename;
    private String phoneticAudioUrl;
    private String phoneticAudioOriginalUrl;
    private String phoneticUsAudioFilename;
    private String phoneticUsAudioUrl;
    private String phoneticUsAudioOriginalUrl;
    private String phoneticUkAudioFilename;
    private String phoneticUkAudioUrl;
    private String phoneticUkAudioOriginalUrl;
    private String partOfSpeech;
    private String synonyms;
    private String translation;
    private String meaningClue;
    private String meaning;
    private String easyMeaning;
    private String sentenceOne;
    private String sentenceTwo;
    private String explanation;
    private String difficultyLevel;
    private String dictionaryUrl;
    private String additionalInfo;
    private Integer term;
    private Integer week;
    private String channel;
    private String tags;
    private String lang;
    private Integer displayOrder;
    private String updatedAt;
}
