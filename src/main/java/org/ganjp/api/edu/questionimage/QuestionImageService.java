package org.ganjp.api.edu.questionimage;

import lombok.RequiredArgsConstructor;
import org.ganjp.api.cms.util.CmsUtil;
import org.ganjp.api.edu.common.EduFileService;
import org.ganjp.api.edu.common.EduQuestionImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionImageService {
    private final QuestionImageRepository repository;
    private final QuestionImageUploadProperties uploadProperties;
    private final EduFileService fileService;

    @Value("${edu.question-image.base-url:}")
    private String imageBaseUrl;

    public List<EduQuestionImageResponse> search(String multipleChoiceQuestionId, String freeTextQuestionId,
            String trueFalseQuestionId, String fillBlankQuestionId, QuestionImage.Language lang, Boolean isActive) {
        return repository.search(multipleChoiceQuestionId, freeTextQuestionId, trueFalseQuestionId, fillBlankQuestionId,
                lang, isActive).stream().map(this::map).toList();
    }

    public EduQuestionImageResponse getById(String id) {
        return repository.findById(id).map(this::map).orElse(null);
    }

    public File getImageFile(String filename) {
        if (!repository.existsByFilename(filename)) throw new IllegalArgumentException("Question image not found");
        return fileService.resolveExistingFile(uploadProperties.getDirectory(), filename);
    }

    private EduQuestionImageResponse map(QuestionImage image) {
        return EduQuestionImageResponse.builder()
                .id(image.getId()).multipleChoiceQuestionId(image.getMultipleChoiceQuestionId())
                .freeTextQuestionId(image.getFreeTextQuestionId()).trueFalseQuestionId(image.getTrueFalseQuestionId())
                .fillBlankQuestionId(image.getFillBlankQuestionId()).filename(image.getFilename())
                .originalUrl(image.getOriginalUrl()).fileUrl(CmsUtil.joinBaseAndPath(imageBaseUrl, image.getFilename()))
                .width(image.getWidth()).height(image.getHeight()).lang(image.getLang() != null ? image.getLang().name() : null)
                .displayOrder(image.getDisplayOrder()).updatedAt(image.getUpdatedAt() != null ? image.getUpdatedAt().toString() : null)
                .build();
    }
}
