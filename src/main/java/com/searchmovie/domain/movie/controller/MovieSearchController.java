package com.searchmovie.domain.movie.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.domain.movie.model.response.MovieSearchResponse;
import com.searchmovie.domain.movie.model.response.SimplePageResponse;
import com.searchmovie.domain.movie.service.MovieSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MovieSearchController {

    private final MovieSearchService movieSearchService;

    @GetMapping("/v2/movies/search")
    public ResponseEntity<CommonResponse<SimplePageResponse<MovieSearchResponse>>> searchMovieApi(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String genreKeyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateEnd,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<MovieSearchResponse> result =
                movieSearchService.searchMovie(title, director, genreKeyword, releaseDateStart, releaseDateEnd, page, size);

        return ResponseEntity.ok(CommonResponse.success(SimplePageResponse.from(result), "success"));
    }



}
