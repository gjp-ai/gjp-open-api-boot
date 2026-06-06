package org.ganjp.api.edu.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EduQuestionImageResponse {
    private String id;
    private String multipleChoiceQuestionId;
    private String freeTextQuestionId;
    private String trueFalseQuestionId;
    private String fillBlankQuestionId;
    private String filename;
    private String originalUrl;
    private String fileUrl;
    private Integer width;
    private Integer height;
    private String lang;
    private Integer displayOrder;
    private String updatedAt;
}
