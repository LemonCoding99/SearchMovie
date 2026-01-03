package com.searchmovie.domain.movie.dto.response;

import com.searchmovie.domain.movie.entity.Genre;
import com.searchmovie.domain.movie.entity.Movie;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MovieGetResponse {

    private final Long id;
    private final String title;
    private final String director;
    private final LocalDate releaseDate;
    private final List<String> genres;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;


    public MovieGetResponse(Long id, String title, String director, LocalDate releaseDate, List<String> genres, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static MovieGetResponse of(Movie movie, List<Genre> genres) {
        List<String> names = new ArrayList<>(genres.size());
        for (Genre genre : genres) {
            names.add(genre.getName());
        }

        return new MovieGetResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getReleaseDate(),
                names,
                movie.getCreatedAt(),
                movie.getUpdatedAt()
        );
    }

}
