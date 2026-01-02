package com.searchmovie.domain.movie.repository;

import com.searchmovie.domain.movie.entity.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, Long> {
    boolean existsByMovieIdAndGenreId(Long movieId, Long genreId);

}
