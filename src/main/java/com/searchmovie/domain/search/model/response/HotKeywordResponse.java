package com.searchmovie.domain.search.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class HotKeywordResponse {
    private final int rank;
    private final String keyword;
    private final String title;
    private final String genre;
    private final String director;
    private final LocalDate releaseDate;
    private final Long score;

//    @JsonCreator
//    public HotKeywordResponse(
//            @JsonProperty("rank") int rank,
//            @JsonProperty("keyword") String keyword,
//            @JsonProperty("title") String title,
//            @JsonProperty("genre") String genre,
//            @JsonProperty("director") String director,
//            @JsonProperty("releaseDate") LocalDate releaseDate,
//            @JsonProperty("score") Long score) {
//        this.rank = rank;
//        this.keyword = keyword;
//        this.title = title;
//        this.genre = genre;
//        this.director = director;
//        this.releaseDate = releaseDate;
//        this.score = score;
//    }

}
