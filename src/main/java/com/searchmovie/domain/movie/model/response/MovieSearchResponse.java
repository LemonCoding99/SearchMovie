package com.searchmovie.domain.movie.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor // Redis 역직렬화 시 객체를 생성할 수 있게 함
@AllArgsConstructor // 기존 생성 흐름 유지용
public class MovieSearchResponse {
    private Long id;
    private String title;
    private List<String> genres;  // 모든 장르 가져오기
    private String director;
    private LocalDate releaseDate;
}