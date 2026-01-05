package com.searchmovie.domain.movie.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class MovieCreateRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String director;

    private LocalDate releaseDate;

    private List<@NotBlank @Size(max = 50) String> genres;
}