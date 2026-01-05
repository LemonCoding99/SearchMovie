package com.searchmovie.domain.movie.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class MovieSearchResponse {
    private final Long id;
    private final String title;
    private final List<String> genres;  // 모든 장르 가져오기
    private final String director;
    private final LocalDate releaseDate;
}