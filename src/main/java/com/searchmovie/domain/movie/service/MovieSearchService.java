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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieSearchService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final SearchRepository searchRepository;  // 나중에 SearchLogRepository로 변경
    private final MovieSearchCacheService movieSearchCacheService;


    // 영화 전체 검색(캐시 사용하지 않은 V1)
    @Transactional(readOnly = true)
    public SimplePageResponse<MovieSearchResponse> searchMovie1(String title, String director, String genreKeyword, LocalDate releaseDateStart, LocalDate releaseDateEnd, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MovieSearchResponse> response = movieRepository.search(title, director, genreKeyword, releaseDateStart, releaseDateEnd, pageable);

        return SimplePageResponse.from(response);
    }

    // 영화 전체 검색(캐시 사용한 V2)
    @Transactional(readOnly = true)
    @Cacheable(
            value = "searchCache",
            // 공백제거, 문자열 처리
            key = "'movieSearch:'"
                    + " + 'title=' + (#title == null ? '' : #title.trim())"
                    + " + '|director=' + (#director == null ? '' : #director.trim())"
                    + " + '|genre=' + (#genreKeyword == null ? '' : #genreKeyword.trim())"
                    + " + '|start=' + (#releaseDateStart == null ? '' : #releaseDateStart.toString())"
                    + " + '|end=' + (#releaseDateEnd == null ? '' : #releaseDateEnd.toString())"
                    + " + '|page=' + #page"
                    + " + '|size=' + #size"
    )  // KeyGenerator를 쓴 버전으로 리팩토링 해보기 ➕
    public SimplePageResponse<MovieSearchResponse> searchMovie2(String title, String director, String genreKeyword, LocalDate releaseDateStart, LocalDate releaseDateEnd, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MovieSearchResponse> response = movieRepository.search(title, director, genreKeyword, releaseDateStart, releaseDateEnd, pageable);

        return SimplePageResponse.from(response);
    }

    // 영화 전체 검색(Redis 캐시 사용한 V3)
    @Transactional(readOnly = true)
    public SimplePageResponse<MovieSearchResponse> searchMovie3(String title, String director, String genreKeyword, LocalDate releaseDateStart, LocalDate releaseDateEnd, int page, int size) {
        // 1. 캐시가 있나요?
        SimplePageResponse<MovieSearchResponse> cached =
                movieSearchCacheService.getSearchCache(title, director, genreKeyword, releaseDateStart, releaseDateEnd, page, size);
        if (cached != null) {
            log.info(" Redis Search Cache Hit ");
            return cached;
        }
        log.info(" Redis Search Cache Miss ");

        // 2. 캐시가 없으면 DB에서 직접 조회
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieSearchResponse> resultPage = movieRepository.search(title, director, genreKeyword, releaseDateStart, releaseDateEnd, pageable);
        SimplePageResponse<MovieSearchResponse> response = SimplePageResponse.from(resultPage);

        // 3. DB에서 조회한 값 캐시에 저장하기
        movieSearchCacheService.saveSearchCache(title, director, genreKeyword, releaseDateStart, releaseDateEnd, page, size, response);

        return response;
    }


    // 영화 검색 로그 생성
    @Transactional
    @Cacheable(value = "searchCache", key = "'movie:' + #movieId")  // 같은 movieId일 경우 캐시 저장되어 빨리 가져옴
    public MovieSelectCreateResponse createSelect(String keyword, Long userId, Long movieId) {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new CustomException(ExceptionCode.SEARCH_KEYWORD_REQUIRED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MOVIE_NOT_FOUND));

        SearchLog newSearchLog = searchRepository.save(new SearchLog(user, movie, keyword));

        return MovieSelectCreateResponse.from(newSearchLog);
    }
}