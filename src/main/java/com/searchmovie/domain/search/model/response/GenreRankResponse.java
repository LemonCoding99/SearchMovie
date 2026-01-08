package com.searchmovie.domain.search.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GenreRankResponse {

    private final int rank;
    private final String genre;
    private final Long score;
}
