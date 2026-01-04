package com.searchmovie.domain.movie.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.domain.movie.model.response.MovieSelectCreateResponse;
import com.searchmovie.domain.movie.model.response.MovieSearchResponse;
import com.searchmovie.domain.movie.model.response.SimplePageResponse;
import com.searchmovie.domain.movie.service.MovieSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MovieSearchController {

    private final MovieSearchService movieSearchService;

    // 영화 전체 검색 Api(캐시 사용하지 않은 버전)
    @GetMapping("/v1/movies/search")
    public ResponseEntity<CommonResponse<SimplePageResponse<MovieSearchResponse>>> searchMovie1Api(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String genreKeyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateEnd,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        SimplePageResponse<MovieSearchResponse> response = movieSearchService.searchMovie1(title, director, genreKeyword, releaseDateStart, releaseDateEnd, page, size);

        return ResponseEntity.ok(CommonResponse.success(response, "success"));
    }

    // 영화 전체 검색 Api
    @GetMapping("/v2/movies/search")
    public ResponseEntity<CommonResponse<SimplePageResponse<MovieSearchResponse>>> searchMovie2Api(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String genreKeyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateEnd,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        SimplePageResponse<MovieSearchResponse> response = movieSearchService.searchMovie2(title, director, genreKeyword, releaseDateStart, releaseDateEnd, page, size);

        return ResponseEntity.ok(CommonResponse.success(response, "success"));
    }

    // 사용자가 검색 결과를 바탕으로 선택한 영화를 로그로 저장하는 Api
    @PostMapping("/movies/{movieId}/select")
    public ResponseEntity<CommonResponse<MovieSelectCreateResponse>> createSelectApi(
            @RequestParam String keyword,
            @RequestParam Long userId,  // SpringSecurity 추가 후 @AuthenticationPrincipal로 변경하기➕
            @PathVariable Long movieId
    ) {
        MovieSelectCreateResponse response = movieSearchService.createSelect(keyword, userId, movieId);

        return ResponseEntity.ok(CommonResponse.success(response, "success"));
    }

}
