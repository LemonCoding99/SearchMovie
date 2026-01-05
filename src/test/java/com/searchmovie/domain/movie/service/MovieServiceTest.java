package com.searchmovie.domain.movie.service;

import com.searchmovie.domain.movie.entity.Genre;
import com.searchmovie.domain.movie.entity.Movie;
import com.searchmovie.domain.movie.entity.MovieGenre;
import com.searchmovie.domain.movie.model.request.MovieCreateRequest;
import com.searchmovie.domain.movie.model.response.MovieCreateResponse;
import com.searchmovie.domain.movie.model.response.MovieGetResponse;
import com.searchmovie.domain.movie.repository.GenreRepository;
import com.searchmovie.domain.movie.repository.MovieGenreRepository;
import com.searchmovie.domain.movie.repository.MovieRepository;
import com.searchmovie.domain.movie.support.GenreNormalizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private MovieGenreRepository movieGenreRepository;

    @Mock
    private GenreNormalizer genreNormalizer;

    @InjectMocks
    private MovieService movieService;

    @Test
    @DisplayName("영화 생성 성공")
    void createMovie() {
        // given
        MovieCreateRequest request = mock(MovieCreateRequest.class);
        when(request.getTitle()).thenReturn("테스트 무비");
        when(request.getDirector()).thenReturn("테스트 감독");
        when(request.getReleaseDate()).thenReturn(LocalDate.of(2026, 1, 5));
        when(request.getGenres()).thenReturn(Arrays.asList("액션", "SF"));

        Movie savedMovie = mock(Movie.class);
        when(savedMovie.getId()).thenReturn(1L);
        when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

        when(genreNormalizer.normalize(anyList())).thenReturn(Arrays.asList("액션", "SF"));

        when(genreRepository.findByName("액션")).thenReturn(Optional.empty());
        when(genreRepository.findByName("SF")).thenReturn(Optional.empty());

        Genre g1 = mock(Genre.class);
        Genre g2 = mock(Genre.class);
        when(g1.getId()).thenReturn(10L);
        when(g2.getId()).thenReturn(20L);

        when(genreRepository.save(any(Genre.class)))
                .thenReturn(g1)
                .thenReturn(g2);

        // when
        MovieCreateResponse res = movieService.createMovie(request);

        // then
        assertNotNull(res);
        verify(movieRepository).save(any(Movie.class));
        verify(movieGenreRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("영화 단 건 조회 성공")
    void getMovie() {
        // given
        Movie movie = mock(Movie.class);
        when(movieRepository.findById(5L)).thenReturn(Optional.of(movie));

        when(movieGenreRepository.findAllByMovieId(5L)).thenReturn(Arrays.asList(
                new MovieGenre(5L, 100L),
                new MovieGenre(5L, 200L)
        ));

        Genre g1 = mock(Genre.class);
        Genre g2 = mock(Genre.class);
        when(genreRepository.findAllById(anyList())).thenReturn(Arrays.asList(g1, g2));

        // when
        MovieGetResponse res = movieService.getMovie(5L);

        // then
        assertNotNull(res);
        verify(movieRepository).findById(5L);
        verify(movieGenreRepository).findAllByMovieId(5L);
        verify(genreRepository).findAllById(anyList());
    }

    @Test
    @DisplayName("영화 목록 조회")
    void getMovies() {
    }

    @Test
    void updateMovie() {
    }

    @Test
    @DisplayName("영화 삭제 성공")
    void deleteMovie() {
        // given
        Movie movie = mock(Movie.class);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        // when
        movieService.deleteMovie(1L);

        // then
        verify(movieGenreRepository).deleteAllByMovieId(1L);
        verify(movieRepository).deleteById(1L);
    }
}