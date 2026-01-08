package com.searchmovie.domain.search.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class SynthesisRankResponse {
    private final int rank;
    private final String keyword;
    private final String title;
    private final String genre;
    private final String director;
    private final LocalDate releaseDate;
    private final Long score;

    public SynthesisRankResponse(int rank, RepositoryInSynthesisDto dto) {
        this.rank = rank;
        this.keyword = dto.getKeyword();
        this.title = dto.getTitle();
        this.genre = dto.getGenre();
        this.director = dto.getDirector();
        this.releaseDate = dto.getReleaseDate();
        this.score = dto.getScore();
    }

}
