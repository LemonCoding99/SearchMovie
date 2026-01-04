package com.searchmovie.domain.movie.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MovieBaseResponse {
    private final Long id;
    private final String title;
    private final String director;
    private final LocalDate releaseDate;
}
