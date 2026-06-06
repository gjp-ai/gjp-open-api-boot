package org.ganjp.api.edu.vocabulary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VocabularyRepository extends JpaRepository<Vocabulary, String> {
    @Query("SELECT v FROM Vocabulary v WHERE v.channel = :channel AND " +
            "(:name IS NULL OR TRIM(:name) = '' OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:lang IS NULL OR v.lang = :lang) AND " +
            "(:tags IS NULL OR TRIM(:tags) = '' OR LOWER(v.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) AND " +
            "(:isActive IS NULL OR v.isActive = :isActive) AND " +
            "(:term IS NULL OR v.term = :term) AND " +
            "(:week IS NULL OR v.week = :week) AND " +
            "(:difficultyLevel IS NULL OR TRIM(:difficultyLevel) = '' OR v.difficultyLevel = :difficultyLevel) AND " +
            "(:partOfSpeech IS NULL OR TRIM(:partOfSpeech) = '' OR LOWER(v.partOfSpeech) LIKE LOWER(CONCAT('%', :partOfSpeech, '%')))")
    Page<Vocabulary> search(@Param("channel") String channel, @Param("name") String name,
            @Param("lang") Vocabulary.Language lang, @Param("tags") String tags, @Param("isActive") Boolean isActive,
            @Param("term") Integer term, @Param("week") Integer week, @Param("difficultyLevel") String difficultyLevel,
            @Param("partOfSpeech") String partOfSpeech, Pageable pageable);

    @Query("SELECT v FROM Vocabulary v WHERE v.channel = :channel AND " +
            "(:name IS NULL OR TRIM(:name) = '' OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:lang IS NULL OR v.lang = :lang) AND " +
            "(:tags IS NULL OR TRIM(:tags) = '' OR LOWER(v.tags) LIKE LOWER(CONCAT('%', :tags, '%'))) AND " +
            "(:isActive IS NULL OR v.isActive = :isActive) AND " +
            "(:term IS NULL OR v.term = :term) AND " +
            "(:week IS NULL OR v.week = :week) AND " +
            "(:difficultyLevel IS NULL OR TRIM(:difficultyLevel) = '' OR v.difficultyLevel = :difficultyLevel) AND " +
            "(:partOfSpeech IS NULL OR TRIM(:partOfSpeech) = '' OR LOWER(v.partOfSpeech) LIKE LOWER(CONCAT('%', :partOfSpeech, '%'))) AND " +
            "(:updatedAfter IS NULL OR v.updatedAt > :updatedAfter) ORDER BY v.displayOrder ASC")
    List<Vocabulary> findAll(@Param("channel") String channel, @Param("name") String name,
            @Param("lang") Vocabulary.Language lang, @Param("tags") String tags, @Param("isActive") Boolean isActive,
            @Param("term") Integer term, @Param("week") Integer week, @Param("difficultyLevel") String difficultyLevel,
            @Param("partOfSpeech") String partOfSpeech, @Param("updatedAfter") LocalDateTime updatedAfter);

    boolean existsByPhoneticUsAudioFilenameOrPhoneticUkAudioFilename(String usFilename, String ukFilename);
}
