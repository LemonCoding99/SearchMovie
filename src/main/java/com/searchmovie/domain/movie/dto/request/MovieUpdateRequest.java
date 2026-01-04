package com.searchmovie.domain.movie.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class MovieUpdateRequest {
    private String title;

    private String director;

    private LocalDate releaseDate;

    private List<String> genres;
}
