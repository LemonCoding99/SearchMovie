package com.searchmovie.domain.search.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class SearchKeywordResponse {
    private final String keyword;
    private final String title;
    private final String genre;
    private final String director;
    private final LocalDate releaseDate;
    private final Long score;
}
