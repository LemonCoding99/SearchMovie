package com.searchmovie.domain.movie.repository;

import com.searchmovie.domain.movie.model.response.MovieSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MovieSearchCustomRepository {

    // 영화 검색 기능
    Page<MovieSearchResponse> search(String title,
                                     String director,
                                     String genreKeyword,
                                     LocalDate releaseDateStart,
                                     LocalDate releaseDateEnd,
                                     Pageable pageable);
}
