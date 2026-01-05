package com.searchmovie.domain.movie.repository;

import com.searchmovie.domain.movie.entity.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, Long> {

    boolean existsByMovieIdAndGenreId(Long movieId, Long genreId);

    List<MovieGenre> findAllByMovieId(Long id);

    List<MovieGenre> findAllByMovieIdIn(List<Long> movieIds);

    void deleteAllByMovieId(Long id);
}
