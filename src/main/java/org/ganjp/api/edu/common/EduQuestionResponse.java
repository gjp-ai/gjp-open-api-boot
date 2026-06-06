package org.ganjp.api.edu.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EduQuestionResponse {
    private String id;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String description;
    private String questionA;
    private String answerA;
    private String questionB;
    private String answerB;
    private String questionC;
    private String answerC;
    private String questionD;
    private String answerD;
    private String questionE;
    private String answerE;
    private String questionF;
    private String answerF;
    private String explanation;
    private String difficultyLevel;
    private Integer failCount;
    private Integer successCount;
    private String gradeLevel;
    private String subject;
    private String topic;
    private Integer term;
    private Integer week;
    private String channel;
    private String tags;
    private String lang;
    private Integer displayOrder;
    private String updatedAt;
}
