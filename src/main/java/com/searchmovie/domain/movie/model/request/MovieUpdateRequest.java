package com.searchmovie.domain.movie.model.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class MovieUpdateRequest {

    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    private String title;

    @Size(max = 100, message = "감독명은 100자 이하여야 합니다.")
    private String director;

    private LocalDate releaseDate;

    private List<String> genres;
}