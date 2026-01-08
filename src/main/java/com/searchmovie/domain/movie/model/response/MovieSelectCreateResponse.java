package com.searchmovie.domain.movie.model.response;

import com.searchmovie.domain.search.entity.HotKeyword;
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

    public static MovieSelectCreateResponse from(HotKeyword hotKeyword) {
        return new MovieSelectCreateResponse(
                hotKeyword.getId(),
                hotKeyword.getUserId(),
                hotKeyword.getMovieId(),
//                hotKeyword.getUser().getId(),
//                hotKeyword.getMovie().getId(),
                hotKeyword.getKeyword(),
                hotKeyword.getSearchedAt()
        );
    }
}
