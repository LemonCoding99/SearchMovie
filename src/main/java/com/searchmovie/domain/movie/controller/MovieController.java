package com.searchmovie.domain.movie.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.common.model.PageResponse;
import com.searchmovie.domain.movie.model.request.MovieCreateRequest;
import com.searchmovie.domain.movie.model.request.MovieUpdateRequest;
import com.searchmovie.domain.movie.model.response.MovieCreateResponse;
import com.searchmovie.domain.movie.model.response.MovieGetResponse;
import com.searchmovie.domain.movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MovieController {

    private final MovieService movieService;

    @PostMapping("/auth/movies")
    public ResponseEntity<CommonResponse<MovieCreateResponse>> createMovie(@Valid @RequestBody MovieCreateRequest request) {
        MovieCreateResponse response =  movieService.createMovie(request);
        CommonResponse<MovieCreateResponse> commonResponse = new CommonResponse<>(true, "영화 생성 성공", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<CommonResponse<MovieGetResponse>> getMovie(@PathVariable Long id) {
        MovieGetResponse response = movieService.getMovie(id);
        CommonResponse<MovieGetResponse> commonResponse = new CommonResponse<>(true, "영화 조회 성공", response);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @GetMapping("/movies")
    public ResponseEntity<CommonResponse<PageResponse<MovieGetResponse>>> getMovies(Pageable pageable) {
        PageResponse<MovieGetResponse> response = movieService.getMovies(pageable);
        CommonResponse<PageResponse<MovieGetResponse>> commonResponse = new CommonResponse<>(true, "영화 전체 조회 성공", response);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
    @PutMapping("/auth/movies/{id}")
    public ResponseEntity<CommonResponse<MovieGetResponse>> updateMovie(
            @PathVariable Long id,
            @RequestBody MovieUpdateRequest request
    ) {
        MovieGetResponse response = movieService.updateMovie(id, request);
        return ResponseEntity.ok(new CommonResponse<>(true, "영화 수정 성공", response));
    }
}
