package com.searchmovie.domain.movie.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.common.model.PageResponse;
import com.searchmovie.domain.movie.model.request.MovieCreateRequest;
import com.searchmovie.domain.movie.model.request.MovieUpdateRequest;
import com.searchmovie.domain.movie.model.response.MovieCreateResponse;
import com.searchmovie.domain.movie.model.response.MovieGetResponse;
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
        // 영화 존재여부 확인
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.MOVIE_NOT_FOUND));

        // 해당 영화 장르 조회
        List<MovieGenre> movieGenreList = movieGenreRepository.findAllByMovieId(id);

        // 매핑으로 장르 id 뽑기
        List<Long> genreIdList = new ArrayList<>(movieGenreList.size());
        for (MovieGenre movieGenre : movieGenreList) {
            genreIdList.add(movieGenre.getGenreId());
        }

        // 뽑은 id로 조회
        List<Genre> genres = genreIdList.isEmpty()
                ? List.of()
                : genreRepository.findAllById(genreIdList);

        // 응답
        return MovieGetResponse.of(movie, genres);
    }

    // 영화 전체조회
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

        // 장르 매핑을 위해 맵 생성
        Map<Long, List<Genre>> genresByMovieId = new HashMap<>();

        // 영화가 없으면 날리지않음
        if (!movieIds.isEmpty()) {
            List<MovieGenre> mappings = movieGenreRepository.findAllByMovieIdIn(movieIds); // 매핑 영화랑 장르랑

            // 장르id만 모음 중복제거를 위해 set사용
            Set<Long> genreIds = new HashSet<>();
            for (MovieGenre movieGenre : mappings) {
                genreIds.add(movieGenre.getGenreId());
            }

            // 장르 조회 후 그 id를 변환
            Map<Long, Genre> genreById = new HashMap<>();
            if (!genreIds.isEmpty()) {
                List<Genre> genres = genreRepository.findAllById(genreIds);
                for (Genre genre : genres) {
                    genreById.put(genre.getId(), genre);
                }
            }

            // 장르 채우기
            for (MovieGenre movieGenre : mappings) {
                Genre genre = genreById.get(movieGenre.getGenreId());
                if (genre == null) continue;

                List<Genre> list = genresByMovieId.computeIfAbsent(movieGenre.getMovieId(), k -> new ArrayList<>());
                list.add(genre);
            }
        }

        Page<MovieGetResponse> dtoPage = moviePage.map(movie -> {
            List<Genre> genres = genresByMovieId.get(movie.getId());
            if (genres == null) genres = List.of();
            return MovieGetResponse.of(movie, genres);
        });

        return PageResponse.from(dtoPage);
    }

    // 영화 수정
    @Transactional
    public MovieGetResponse updateMovie(Long id, MovieUpdateRequest request) {

        // 영화 조회
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.MOVIE_NOT_FOUND));

        // null 이면 수정x null아니면 공백 제거
        String title = request.getTitle();
        if (title != null) {
            if (title.isBlank()) throw new CustomException(ExceptionCode.INVALID_MOVIE_TITLE);
            title = title.trim();
        }

        // null 이면 수정x null아니면 공백 제거
        String director = request.getDirector();
        if (director != null) {
            if (director.isBlank()) throw new CustomException(ExceptionCode.INVALID_MOVIE_DIRECTOR);
            director = director.trim();
        }

        movie.update(title, director, request.getReleaseDate());

        List<String> rawGenres = request.getGenres();
        List<Genre> resultGenres;

        // null이여서 미수정 경우
        if (rawGenres == null) {
            List<MovieGenre> movieGenreList = movieGenreRepository.findAllByMovieId(id);

            List<Long> genreIdList = new ArrayList<>(movieGenreList.size());
            for (MovieGenre mg : movieGenreList) {
                genreIdList.add(mg.getGenreId());
            }

            resultGenres = genreIdList.isEmpty()
                    ? List.of()
                    : genreRepository.findAllById(genreIdList);
        // 수정할 경우
        } else {
            List<String> normalized = genreNormalizer.normalize(rawGenres);

            // 의미없는 값 줬을 경우 예외처리
            if (!rawGenres.isEmpty() && normalized.isEmpty()) {
                throw new CustomException(ExceptionCode.INVALID_GENRE_NAME);
            }

            // 디비에 정규화된 장르(재사용 또는 생성위해)
            List<Genre> genres = new ArrayList<>(normalized.size());
            for (String name : normalized) {
                if (name == null || name.isBlank()) {
                    throw new CustomException(ExceptionCode.INVALID_GENRE_NAME);
                }
                genres.add(findOrCreateGenre(name));
            }

            // 기존 매핑되어있던 장르id 삭제
            movieGenreRepository.deleteAllByMovieId(id);

            // 삭제 후 재삽입
            List<MovieGenre> mappings = new ArrayList<>(genres.size());
            for (Genre genre : genres) {
                mappings.add(new MovieGenre(id, genre.getId()));
            }
            movieGenreRepository.saveAll(mappings);

            // 새로운 장르들
            resultGenres = genres;
        }

        // 응답
        return MovieGetResponse.of(movie, resultGenres);
    }

    // 영화 삭제
    @Transactional
    public void deleteMovie(Long id) {
        // 존재확인
        movieRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.MOVIE_NOT_FOUND));
        // 장르 먼저 삭제
        movieGenreRepository.deleteAllByMovieId(id);
        // 영화 삭제
        movieRepository.deleteById(id);
    }
}