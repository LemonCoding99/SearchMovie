package com.searchmovie.domain.movie.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.movie.dto.request.MovieCreateRequest;
import com.searchmovie.domain.movie.dto.response.MovieCreateResponse;
import com.searchmovie.domain.movie.entity.Genre;
import com.searchmovie.domain.movie.entity.Movie;
import com.searchmovie.domain.movie.entity.MovieGenre;
import com.searchmovie.domain.movie.repository.GenreRepository;
import com.searchmovie.domain.movie.repository.MovieGenreRepository;
import com.searchmovie.domain.movie.repository.MovieRepository;
import com.searchmovie.domain.movie.support.GenreNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final GenreNormalizer genreNormalizer;

    @Transactional
    public MovieCreateResponse createMovie(MovieCreateRequest request) {

        // 1) 영화 생성
        Movie movie = movieRepository.save(
                new Movie(request.getTitle(), request.getDirector(), request.getReleaseDate())
        );

        // 2) 장르 정규화 (위임)
        List<String> genreNames = genreNormalizer.normalize(request.getGenres());

        // 3) 장르 조회 또는 생성
        List<Genre> genres = new ArrayList<>(genreNames.size());
        for (String name : genreNames) {
            genres.add(findOrCreateGenre(name));
        }

        // 4) 매핑 저장
        List<MovieGenre> mappings = new ArrayList<>(genres.size());
        for (Genre genre : genres) {
            mappings.add(new MovieGenre(movie.getId(), genre.getId()));
        }
        movieGenreRepository.saveAll(mappings);

        // 5) 응답
        return MovieCreateResponse.of(movie, genres);
    }

    private Genre findOrCreateGenre(String name) {
        if (name.isEmpty()) {
            throw new CustomException(ExceptionCode.INVALID_GENRE_NAME);
        }

        return genreRepository.findByName(name)
                .orElseGet(() -> genreRepository.save(new Genre(name)));
    }
}
