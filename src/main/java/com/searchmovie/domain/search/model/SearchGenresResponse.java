package com.searchmovie.domain.search.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchGenresResponse {
    private final String genre;
    private final Long score;
}
