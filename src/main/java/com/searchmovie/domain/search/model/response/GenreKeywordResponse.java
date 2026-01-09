package com.searchmovie.domain.search.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GenreKeywordResponse {

    private final int rank;
    private final String genre;
    private final Long score;
//
//    @JsonCreator
//    public GenreKeywordResponse(
//            @JsonProperty("rank") int rank,
//            @JsonProperty("genre") String genre,
//            @JsonProperty("score") Long score) {
//        this.rank = rank;
//        this.genre = genre;
//        this.score = score;
//    }
}
