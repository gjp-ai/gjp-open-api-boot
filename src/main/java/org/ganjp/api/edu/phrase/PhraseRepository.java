package org.ganjp.api.edu.phrase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PhraseRepository extends JpaRepository<Phrase, String> {
    @Query("SELECT p FROM Phrase p WHERE p.channel = :channel AND " +
            "(:name IS NULL OR TRIM(:name) = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:lang IS NULL OR p.lang = :lang) AND " +
            "(:tags IS NULL OR TRIM(:tags) = '' OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) AND " +
            "(:isActive IS NULL OR p.isActive = :isActive) AND (:term IS NULL OR p.term = :term) AND " +
            "(:week IS NULL OR p.week = :week) AND " +
            "(:difficultyLevel IS NULL OR TRIM(:difficultyLevel) = '' OR p.difficultyLevel = :difficultyLevel)")
    Page<Phrase> search(@Param("channel") String channel, @Param("name") String name,
            @Param("lang") Phrase.Language lang, @Param("tags") String tags, @Param("isActive") Boolean isActive,
            @Param("term") Integer term, @Param("week") Integer week, @Param("difficultyLevel") String difficultyLevel,
            Pageable pageable);

    @Query("SELECT p FROM Phrase p WHERE p.channel = :channel AND " +
            "(:name IS NULL OR TRIM(:name) = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:lang IS NULL OR p.lang = :lang) AND " +
            "(:tags IS NULL OR TRIM(:tags) = '' OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) AND " +
            "(:isActive IS NULL OR p.isActive = :isActive) AND (:term IS NULL OR p.term = :term) AND " +
            "(:week IS NULL OR p.week = :week) AND " +
            "(:difficultyLevel IS NULL OR TRIM(:difficultyLevel) = '' OR p.difficultyLevel = :difficultyLevel) AND " +
            "(:updatedAfter IS NULL OR p.updatedAt > :updatedAfter) ORDER BY p.displayOrder ASC")
    List<Phrase> findAll(@Param("channel") String channel, @Param("name") String name,
            @Param("lang") Phrase.Language lang, @Param("tags") String tags, @Param("isActive") Boolean isActive,
            @Param("term") Integer term, @Param("week") Integer week, @Param("difficultyLevel") String difficultyLevel,
            @Param("updatedAfter") LocalDateTime updatedAfter);

    boolean existsByPhoneticAudioFilename(String filename);
}
