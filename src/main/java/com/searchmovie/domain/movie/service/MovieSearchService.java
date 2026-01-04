package com.searchmovie.domain.movie.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.movie.model.response.MovieSelectCreateResponse;
import com.searchmovie.domain.movie.entity.Movie;
import com.searchmovie.domain.movie.model.response.MovieSearchResponse;
import com.searchmovie.domain.movie.model.response.SimplePageResponse;
import com.searchmovie.domain.movie.repository.MovieRepository;
import com.searchmovie.domain.search.entity.SearchLog;
import com.searchmovie.domain.search.repository.SearchRepository;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MovieSearchService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final SearchRepository searchRepository;  // 나중에 SearchLogRepository로 변경

    // 영화 전체 검색
    @Transactional(readOnly = true)
    public SimplePageResponse<MovieSearchResponse> searchMovie(String title, String director, String genreKeyword, LocalDate releaseDateStart, LocalDate releaseDateEnd, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<MovieSearchResponse> response = movieRepository.search(title, director, genreKeyword, releaseDateStart, releaseDateEnd, pageable);

        return SimplePageResponse.from(response);

    }


    // 영화 검색 로그 생성
    @Transactional
    public MovieSelectCreateResponse createSelect(String keyword, Long userId, Long movieId) {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색어는 꼭 포함되어야 합니다.");  // custom으로 변경해주기➕
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MOVIE_NOT_FOUND));

        SearchLog newSearchLog = searchRepository.save(new SearchLog(user, movie, keyword));

        return MovieSelectCreateResponse.from(newSearchLog);
    }



}