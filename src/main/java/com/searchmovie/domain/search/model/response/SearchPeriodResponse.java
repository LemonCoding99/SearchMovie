package com.searchmovie.domain.search.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class SearchPeriodResponse {

    private final String keyword;
    private final String title;
    private final String genre;
    private final String director;
    private final LocalDate releaseDate;
    private final Long score;
}
