package com.searchmovie.domain.movie.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.common.model.PageResponse;
import com.searchmovie.domain.movie.dto.request.MovieCreateRequest;
import com.searchmovie.domain.movie.dto.request.MovieUpdateRequest;
import com.searchmovie.domain.movie.dto.response.MovieCreateResponse;
import com.searchmovie.domain.movie.dto.response.MovieGetResponse;
import com.searchmovie.domain.movie.entity.Genre;
import com.searchmovie.domain.movie.entity.Movie;
import com.searchmovie.domain.movie.entity.MovieGenre;
import com.searchmovie.domain.movie.repository.GenreRepository;
import com.searchmovie.domain.movie.repository.MovieGenreRepository;
import com.searchmovie.domain.movie.repository.MovieRepository;
import com.searchmovie.domain.movie.support.GenreNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final GenreNormalizer genreNormalizer;

    // 영화 생성
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

    // 장르 유효성 검증
    private Genre findOrCreateGenre(String name) {
        if (name.isEmpty()) {
            throw new CustomException(ExceptionCode.INVALID_GENRE_NAME);
        }

        // 있는 장르인지 확인 후 있으먼 재사용, 없으면 생성
        return genreRepository.findByName(name)
                .orElseGet(() -> genreRepository.save(new Genre(name)));
    }

    // 영화 단건 조회
    @Transactional(readOnly = true)
    public MovieGetResponse getMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.MOVIE_NOT_FOUND));

        List<MovieGenre> movieGenreList = movieGenreRepository.findAllByMovieId(id);

        List<Long> genreIdList = new ArrayList<>(movieGenreList.size());
        for (MovieGenre movieGenre : movieGenreList) {
            genreIdList.add(movieGenre.getGenreId());
        }

        List<Genre> genres = genreIdList.isEmpty()
                ? List.of()
                : genreRepository.findAllById(genreIdList);

        return MovieGetResponse.of(movie, genres);
    }

    @Transactional(readOnly = true)
    public PageResponse<MovieGetResponse> getMovies(Pageable pageable) {

        // 영화 먼저 페이지로 가져오기
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        List<Movie> movies = moviePage.getContent();

        // 그 페이지의 영화 id 모으기
        List<Long> movieIds = new ArrayList<>(movies.size());
        for (Movie movie : movies) {
            movieIds.add(movie.getId());
        }

        Map<Long, List<Genre>> genresByMovieId = new HashMap<>();

        // 영화가 없으면 날리지않음
        if (!movieIds.isEmpty()) {
            List<MovieGenre> mappings = movieGenreRepository.findAllByMovieIdIn(movieIds); // 매핑 영화랑 장르랑

            // 장르id만 모음 중복제거를 위해 set사용
            Set<Long> genreIds = new HashSet<>();
            for (MovieGenre movieGenre : mappings) {
                genreIds.add(movieGenre.getGenreId());
            }

            Map<Long, Genre> genreById = new HashMap<>();
            if (!genreIds.isEmpty()) {
                List<Genre> genres = genreRepository.findAllById(genreIds);
                for (Genre genre : genres) {
                    genreById.put(genre.getId(), genre);
                }
            }

            for (MovieGenre movieGenre : mappings) {
                Genre genre = genreById.get(movieGenre.getGenreId());
                if (genre == null) continue;

                List<Genre> list = genresByMovieId.computeIfAbsent(movieGenre.getMovieId(), k -> new ArrayList<>());
                list.add(genre);
            }
        }

        Page<MovieGetResponse> dtoPage = moviePage.map(movie -> {
            List<Genre> gs = genresByMovieId.get(movie.getId());
            if (gs == null) gs = List.of();
            return MovieGetResponse.of(movie, gs);
        });

        return PageResponse.from(dtoPage);
    }

    @Transactional
    public MovieGetResponse updateMovie(Long id, MovieUpdateRequest request) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.MOVIE_NOT_FOUND));

        String title = request.getTitle();
        if (title != null) {
            if (title.isBlank()) throw new CustomException(ExceptionCode.INVALID_MOVIE_TITLE);
            title = title.trim();
        }

        String director = request.getDirector();
        if (director != null) {
            if (director.isBlank()) throw new CustomException(ExceptionCode.INVALID_MOVIE_DIRECTOR);
            director = director.trim();
        }

        movie.update(title, director, request.getReleaseDate());

        List<String> rawGenres = request.getGenres();
        List<Genre> resultGenres;

        if (rawGenres == null) {
            List<MovieGenre> movieGenreList = movieGenreRepository.findAllByMovieId(id);

            List<Long> genreIdList = new ArrayList<>(movieGenreList.size());
            for (MovieGenre mg : movieGenreList) {
                genreIdList.add(mg.getGenreId());
            }

            resultGenres = genreIdList.isEmpty()
                    ? List.of()
                    : genreRepository.findAllById(genreIdList);

        } else {
            List<String> normalized = genreNormalizer.normalize(rawGenres);

            if (!rawGenres.isEmpty() && normalized.isEmpty()) {
                throw new CustomException(ExceptionCode.INVALID_GENRE_NAME);
            }

            List<Genre> genres = new ArrayList<>(normalized.size());
            for (String name : normalized) {
                if (name == null || name.isBlank()) {
                    throw new CustomException(ExceptionCode.INVALID_GENRE_NAME);
                }
                genres.add(findOrCreateGenre(name));
            }

            movieGenreRepository.deleteAllByMovieId(id);

            List<MovieGenre> mappings = new ArrayList<>(genres.size());
            for (Genre genre : genres) {
                mappings.add(new MovieGenre(id, genre.getId()));
            }
            movieGenreRepository.saveAll(mappings);

            resultGenres = genres;
        }

        return MovieGetResponse.of(movie, resultGenres);
    }
}
