package org.ganjp.api.edu.common;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public final class EduFavoriteTagUtil {
    private static final String CHINESE_FAVORITE_TAG = "收藏";
    private static final String DEFAULT_FAVORITE_TAG = "Favorite";

    private EduFavoriteTagUtil() {}

    public static String tagForLanguage(Enum<?> lang) {
        return lang != null && "ZH".equals(lang.name()) ? CHINESE_FAVORITE_TAG : DEFAULT_FAVORITE_TAG;
    }

    public static String toggleFavoriteTag(String tags, Enum<?> lang) {
        String favoriteTag = tagForLanguage(lang);
        LinkedHashSet<String> tagSet = Arrays.stream((tags == null ? "" : tags).split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        String existingTag = tagSet.stream()
                .filter(tag -> tag.equalsIgnoreCase(favoriteTag))
                .findFirst()
                .orElse(null);
        if (existingTag == null) {
            tagSet.add(favoriteTag);
        } else {
            tagSet.remove(existingTag);
        }
        return tagSet.isEmpty() ? null : String.join(", ", tagSet);
    }
}
