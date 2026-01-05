package com.searchmovie.domain.movie.repository;

import com.searchmovie.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface MovieRepository extends JpaRepository<Movie, Long>, MovieSearchCustomRepository {

    boolean existsByTitleAndReleaseDate(String title, LocalDate releaseDate);

}