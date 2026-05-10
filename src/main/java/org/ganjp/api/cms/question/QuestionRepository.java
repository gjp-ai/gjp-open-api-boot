package org.ganjp.api.cms.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    @Query("SELECT q FROM Question q WHERE " +
           "(:question IS NULL OR LOWER(q.question) LIKE LOWER(CONCAT('%', :question, '%'))) AND " +
           "(:lang IS NULL OR q.lang = :lang) AND " +
           "(:tags IS NULL OR LOWER(q.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) AND " +
           "(:isActive IS NULL OR q.isActive = :isActive)")
    Page<Question> search(
            @Param("question") String question,
            @Param("lang") Question.Language lang,
            @Param("tags") String tags,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    @Query("SELECT q FROM Question q WHERE " +
        "(:question IS NULL OR LOWER(q.question) LIKE LOWER(CONCAT('%', :question, '%'))) AND " +
        "(:lang IS NULL OR q.lang = :lang) AND " +
        "(:tags IS NULL OR q.tags LIKE CONCAT('%', :tags, '%')) AND " +
        "(:isActive IS NULL OR q.isActive = :isActive) AND " +
        "(:updatedAfter IS NULL OR q.updatedAt > :updatedAfter) " +
        "ORDER BY q.displayOrder ASC")
    List<Question> findAllQuestions(@Param("question") String question,
                 @Param("lang") Question.Language lang,
                 @Param("tags") String tags,
                 @Param("isActive") Boolean isActive,
                 @Param("updatedAfter") java.time.LocalDateTime updatedAfter);
}
