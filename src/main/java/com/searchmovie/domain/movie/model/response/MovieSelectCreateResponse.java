package com.searchmovie.domain.movie.model.response;

import com.searchmovie.domain.search.entity.SearchLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MovieSelectCreateResponse {
    private final Long id;
    private final Long userId;
    private final Long movieId;
    private final String keyword;
    private final LocalDateTime searchedAt;

    public static MovieSelectCreateResponse from(SearchLog searchLog) {
        return new MovieSelectCreateResponse(
                searchLog.getId(),
                searchLog.getUserId(),
                searchLog.getMovieId(),
                searchLog.getKeyword(),
                searchLog.getSearchedAt()
        );
    }
}
