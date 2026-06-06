package org.ganjp.api.edu.sentence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SentenceRepository extends JpaRepository<Sentence, String> {
    @Query("SELECT s FROM Sentence s WHERE s.channel = :channel AND " +
            "(:name IS NULL OR TRIM(:name) = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:lang IS NULL OR s.lang = :lang) AND " +
            "(:tags IS NULL OR TRIM(:tags) = '' OR LOWER(s.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) AND " +
            "(:isActive IS NULL OR s.isActive = :isActive) AND (:term IS NULL OR s.term = :term) AND " +
            "(:week IS NULL OR s.week = :week) AND " +
            "(:difficultyLevel IS NULL OR TRIM(:difficultyLevel) = '' OR s.difficultyLevel = :difficultyLevel)")
    Page<Sentence> search(@Param("channel") String channel, @Param("name") String name,
            @Param("lang") Sentence.Language lang, @Param("tags") String tags, @Param("isActive") Boolean isActive,
            @Param("term") Integer term, @Param("week") Integer week, @Param("difficultyLevel") String difficultyLevel,
            Pageable pageable);

    @Query("SELECT s FROM Sentence s WHERE s.channel = :channel AND " +
            "(:name IS NULL OR TRIM(:name) = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:lang IS NULL OR s.lang = :lang) AND " +
            "(:tags IS NULL OR TRIM(:tags) = '' OR LOWER(s.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) AND " +
            "(:isActive IS NULL OR s.isActive = :isActive) AND (:term IS NULL OR s.term = :term) AND " +
            "(:week IS NULL OR s.week = :week) AND " +
            "(:difficultyLevel IS NULL OR TRIM(:difficultyLevel) = '' OR s.difficultyLevel = :difficultyLevel) AND " +
            "(:updatedAfter IS NULL OR s.updatedAt > :updatedAfter) ORDER BY s.displayOrder ASC")
    List<Sentence> findAll(@Param("channel") String channel, @Param("name") String name,
            @Param("lang") Sentence.Language lang, @Param("tags") String tags, @Param("isActive") Boolean isActive,
            @Param("term") Integer term, @Param("week") Integer week, @Param("difficultyLevel") String difficultyLevel,
            @Param("updatedAfter") LocalDateTime updatedAfter);

    boolean existsByPhoneticAudioFilename(String filename);
}
