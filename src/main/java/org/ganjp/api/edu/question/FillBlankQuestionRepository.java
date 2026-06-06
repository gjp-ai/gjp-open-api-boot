package org.ganjp.api.edu.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FillBlankQuestionRepository extends JpaRepository<FillBlankQuestion, String> {
    @Query("SELECT q FROM FillBlankQuestion q WHERE q.channel = :channel AND " +
            "(:question IS NULL OR TRIM(:question) = '' OR LOWER(q.question) LIKE LOWER(CONCAT('%', :question, '%'))) AND " +
            "(:lang IS NULL OR q.lang = :lang) AND (:tags IS NULL OR TRIM(:tags) = '' OR LOWER(q.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) AND " +
            "(:isActive IS NULL OR q.isActive = :isActive) AND (:term IS NULL OR q.term = :term) AND (:week IS NULL OR q.week = :week) AND " +
            "(:difficultyLevel IS NULL OR TRIM(:difficultyLevel) = '' OR q.difficultyLevel = :difficultyLevel) AND " +
            "(:gradeLevel IS NULL OR TRIM(:gradeLevel) = '' OR q.gradeLevel = :gradeLevel) AND " +
            "(:subject IS NULL OR TRIM(:subject) = '' OR q.subject = :subject) AND (:topic IS NULL OR TRIM(:topic) = '' OR q.topic = :topic)")
    Page<FillBlankQuestion> search(@Param("channel") String channel, @Param("question") String question,
            @Param("lang") FillBlankQuestion.Language lang, @Param("tags") String tags, @Param("isActive") Boolean isActive,
            @Param("term") Integer term, @Param("week") Integer week, @Param("difficultyLevel") String difficultyLevel,
            @Param("gradeLevel") String gradeLevel, @Param("subject") String subject, @Param("topic") String topic, Pageable pageable);

    @Query("SELECT q FROM FillBlankQuestion q WHERE q.channel = :channel AND " +
            "(:question IS NULL OR TRIM(:question) = '' OR LOWER(q.question) LIKE LOWER(CONCAT('%', :question, '%'))) AND " +
            "(:lang IS NULL OR q.lang = :lang) AND (:tags IS NULL OR TRIM(:tags) = '' OR LOWER(q.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) AND " +
            "(:isActive IS NULL OR q.isActive = :isActive) AND (:term IS NULL OR q.term = :term) AND (:week IS NULL OR q.week = :week) AND " +
            "(:difficultyLevel IS NULL OR TRIM(:difficultyLevel) = '' OR q.difficultyLevel = :difficultyLevel) AND " +
            "(:gradeLevel IS NULL OR TRIM(:gradeLevel) = '' OR q.gradeLevel = :gradeLevel) AND " +
            "(:subject IS NULL OR TRIM(:subject) = '' OR q.subject = :subject) AND (:topic IS NULL OR TRIM(:topic) = '' OR q.topic = :topic) AND " +
            "(:updatedAfter IS NULL OR q.updatedAt > :updatedAfter) ORDER BY q.displayOrder ASC")
    List<FillBlankQuestion> findAll(@Param("channel") String channel, @Param("question") String question,
            @Param("lang") FillBlankQuestion.Language lang, @Param("tags") String tags, @Param("isActive") Boolean isActive,
            @Param("term") Integer term, @Param("week") Integer week, @Param("difficultyLevel") String difficultyLevel,
            @Param("gradeLevel") String gradeLevel, @Param("subject") String subject, @Param("topic") String topic,
            @Param("updatedAfter") LocalDateTime updatedAfter);
}
