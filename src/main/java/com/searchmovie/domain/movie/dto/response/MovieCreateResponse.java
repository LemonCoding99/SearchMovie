package com.searchmovie.domain.movie.dto.response;

import com.searchmovie.domain.movie.entity.Genre;
import com.searchmovie.domain.movie.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class MovieCreateResponse {

    private final Long id;
    private final String title;
    private final String director;
    private final LocalDate releaseDate;
    private final List<String> genres;

    public static MovieCreateResponse of(Movie movie, List<Genre> genres) {
        List<String> names = new ArrayList<>(genres.size());
        for (Genre g : genres) {
            names.add(g.getName());
        }

        return new MovieCreateResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getReleaseDate(),
                names
        );
    }
}
