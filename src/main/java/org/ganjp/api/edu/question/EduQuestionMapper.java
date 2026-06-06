package org.ganjp.api.edu.question;

import org.ganjp.api.edu.common.EduQuestionResponse;

public final class EduQuestionMapper {
    private EduQuestionMapper() {}

    public static EduQuestionResponse from(MultipleChoiceQuestion q) {
        return base(q.getId(), q.getQuestion(), q.getAnswer(), q.getExplanation(), q.getDifficultyLevel(), q.getFailCount(),
                q.getSuccessCount(), q.getGradeLevel(), q.getSubject(), q.getTopic(), q.getTerm(), q.getWeek(),
                q.getChannel(), q.getTags(), q.getLang() != null ? q.getLang().name() : null, q.getDisplayOrder(),
                q.getUpdatedAt() != null ? q.getUpdatedAt().toString() : null)
                .optionA(q.getOptionA()).optionB(q.getOptionB()).optionC(q.getOptionC()).optionD(q.getOptionD()).build();
    }

    public static EduQuestionResponse from(FillBlankQuestion q) {
        return base(q.getId(), q.getQuestion(), q.getAnswer(), q.getExplanation(), q.getDifficultyLevel(), q.getFailCount(),
                q.getSuccessCount(), q.getGradeLevel(), q.getSubject(), q.getTopic(), q.getTerm(), q.getWeek(),
                q.getChannel(), q.getTags(), q.getLang() != null ? q.getLang().name() : null, q.getDisplayOrder(),
                q.getUpdatedAt() != null ? q.getUpdatedAt().toString() : null).build();
    }

    public static EduQuestionResponse from(FreeTextQuestion q) {
        return base(q.getId(), q.getQuestion(), q.getAnswer(), q.getExplanation(), q.getDifficultyLevel(), q.getFailCount(),
                q.getSuccessCount(), q.getGradeLevel(), q.getSubject(), q.getTopic(), q.getTerm(), q.getWeek(),
                q.getChannel(), q.getTags(), q.getLang() != null ? q.getLang().name() : null, q.getDisplayOrder(),
                q.getUpdatedAt() != null ? q.getUpdatedAt().toString() : null)
                .description(q.getDescription()).questionA(q.getQuestionA()).answerA(q.getAnswerA())
                .questionB(q.getQuestionB()).answerB(q.getAnswerB()).questionC(q.getQuestionC()).answerC(q.getAnswerC())
                .questionD(q.getQuestionD()).answerD(q.getAnswerD()).questionE(q.getQuestionE()).answerE(q.getAnswerE())
                .questionF(q.getQuestionF()).answerF(q.getAnswerF()).build();
    }

    public static EduQuestionResponse from(TrueFalseQuestion q) {
        return base(q.getId(), q.getQuestion(), q.getAnswer() != null ? q.getAnswer().name() : null, q.getExplanation(),
                q.getDifficultyLevel(), q.getFailCount(), q.getSuccessCount(), q.getGradeLevel(), q.getSubject(),
                q.getTopic(), q.getTerm(), q.getWeek(), q.getChannel(), q.getTags(),
                q.getLang() != null ? q.getLang().name() : null, q.getDisplayOrder(),
                q.getUpdatedAt() != null ? q.getUpdatedAt().toString() : null).build();
    }

    private static EduQuestionResponse.EduQuestionResponseBuilder base(String id, String question, String answer,
            String explanation, String difficultyLevel, Integer failCount, Integer successCount, String gradeLevel,
            String subject, String topic, Integer term, Integer week, String channel, String tags, String lang,
            Integer displayOrder, String updatedAt) {
        return EduQuestionResponse.builder().id(id).question(question).answer(answer).explanation(explanation)
                .difficultyLevel(difficultyLevel).failCount(failCount).successCount(successCount).gradeLevel(gradeLevel)
                .subject(subject).topic(topic).term(term).week(week).channel(channel).tags(tags).lang(lang)
                .displayOrder(displayOrder).updatedAt(updatedAt);
    }
}
