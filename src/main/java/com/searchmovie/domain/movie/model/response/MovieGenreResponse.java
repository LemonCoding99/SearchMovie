package com.searchmovie.domain.movie.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovieGenreResponse {
    private final Long movieId;
    private final String genreName;
}
