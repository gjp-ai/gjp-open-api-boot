package org.ganjp.api.cms.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class CmsUtil {
    private CmsUtil() {}

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Parse a date string to a java.sql.Timestamp.
     */
    public static Timestamp parseTimestamp(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            if (dateStr.contains("T")) {
                return Timestamp.valueOf(LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME));
            } else if (dateStr.length() == 10) {
                return Timestamp.valueOf(LocalDate.parse(dateStr, DATE_FORMATTER).atStartOfDay());
            }
            return Timestamp.valueOf(LocalDateTime.parse(dateStr, DATE_TIME_FORMATTER));
        } catch (DateTimeParseException | IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Parse a date string to a java.time.LocalDateTime.
     */
    public static LocalDateTime parseLocalDateTime(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            if (dateStr.contains("T")) {
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
            } else if (dateStr.length() == 10) {
                return LocalDate.parse(dateStr, DATE_FORMATTER).atStartOfDay();
            }
            return LocalDateTime.parse(dateStr, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException | IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Parse a language string to an enum value. Returns null if blank or invalid.
     */
    public static <E extends Enum<E>> E parseLanguage(String language, Class<E> enumClass) {
        if (language == null || language.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, language.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Validate filename to prevent path traversal attacks.
     * Throws IllegalArgumentException if filename contains path separators or traversal patterns.
     */
    public static void validateFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename must not be empty");
        }
        if (filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            throw new IllegalArgumentException("Invalid filename: " + filename);
        }
    }

    public static Pageable buildPageable(int page, int size, String sort, String direction) {
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(dir, sort));
    }

    /**
     * Join a base URL and a path (filename). Returns null if path is empty.
     */
    public static String joinBaseAndPath(String base, String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        if (base != null && !base.isBlank()) {
            String prefix = base;
            String p = path;
            if (!prefix.endsWith("/") && !p.startsWith("/")) {
                prefix = prefix + "/";
            } else if (prefix.endsWith("/") && p.startsWith("/")) {
                p = p.substring(1);
            }
            return prefix + p;
        }
        if (path.startsWith("http") || path.startsWith("/")) {
            return path;
        }
        return null;
    }

    /**
     * Join base + segment + path. Segment should not be null (e.g. "cover-images").
     */
    public static String joinBasePathWithSegment(String base, String segment, String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        if (path.startsWith("http")) {
            return path;
        }
        String seg = segment == null ? "" : segment;
        while (seg.startsWith("/")) {
            seg = seg.substring(1);
        }
        if (!seg.isEmpty() && !seg.endsWith("/")) {
            seg = seg + "/";
        }
        String p = path.startsWith("/") ? path.substring(1) : path;
        
        if (base != null && !base.isBlank()) {
            String prefix = base;
            if (!prefix.endsWith("/")) {
                prefix = prefix + "/";
            }
            return prefix + seg + p;
        }
        return "/" + seg + p;
    }

    public static String determineContentType(String filename) {
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".png")) {
            return "image/png";
        }
        if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        }
        if (lowerFilename.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lowerFilename.endsWith(".webp")) {
            return "image/webp";
        }
        if (lowerFilename.endsWith(".bmp")) {
            return "image/bmp";
        }
        if (lowerFilename.endsWith(".mp4")) {
            return "video/mp4";
        }
        if (lowerFilename.endsWith(".webm")) {
            return "video/webm";
        }
        if (lowerFilename.endsWith(".ogv") || lowerFilename.endsWith(".ogg")) {
            return "video/ogg";
        }
        if (lowerFilename.endsWith(".mov")) {
            return "video/quicktime";
        }
        if (lowerFilename.endsWith(".mkv")) {
            return "video/x-matroska";
        }
        if (lowerFilename.endsWith(".mp3")) {
            return "audio/mpeg";
        }
        if (lowerFilename.endsWith(".wav")) {
            return "audio/wav";
        }
        if (lowerFilename.endsWith(".flac")) {
            return "audio/flac";
        }
        if (lowerFilename.endsWith(".aac")) {
            return "audio/aac";
        }
        if (lowerFilename.endsWith(".m4a")) {
            return "audio/mp4";
        }
        if (lowerFilename.endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/octet-stream";
    }
}
