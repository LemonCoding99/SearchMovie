package com.searchmovie.domain.movie.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.domain.movie.dto.request.MovieCreateRequest;
import com.searchmovie.domain.movie.dto.response.MovieCreateResponse;
import com.searchmovie.domain.movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MovieController {

    private final MovieService movieService;

    @PostMapping("/movies")
    public ResponseEntity<CommonResponse<MovieCreateResponse>> createMovie(@Valid @RequestBody MovieCreateRequest request) {
        MovieCreateResponse response =  movieService.createMovie(request);
        CommonResponse<MovieCreateResponse> commonResponse = new CommonResponse<>(true, "영화 생성 성공", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }
}
