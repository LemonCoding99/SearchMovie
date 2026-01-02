package com.searchmovie.domain.movie.support;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GenreNormalizer {

    public List<String> normalize(List<String> rawGenres) {
        if (rawGenres == null || rawGenres.isEmpty()) {
            return List.of();
        }

        Set<String> normalized = new LinkedHashSet<>();

        for (String genre : rawGenres) {
            if (genre == null) continue;

            String value = genre.trim();
            if (value.isEmpty()) continue;

            // 정책: 소문자 통일
            normalized.add(value.toLowerCase(Locale.ROOT));
        }

        return new ArrayList<>(normalized);
    }
}