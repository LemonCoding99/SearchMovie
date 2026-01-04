package com.searchmovie.domain.movie.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.searchmovie.domain.movie.model.response.MovieBaseResponse;
import com.searchmovie.domain.movie.model.response.MovieGenreResponse;
import com.searchmovie.domain.movie.model.response.MovieSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static com.searchmovie.domain.movie.entity.QMovie.movie;
import static com.searchmovie.domain.movie.entity.QMovieGenre.movieGenre;
import static com.searchmovie.domain.movie.entity.QGenre.genre;


@RequiredArgsConstructor
public class MovieSearchCustomRepositoryImpl implements MovieSearchCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MovieSearchResponse> search(String title, String director, String genreKeyword, LocalDate releaseDateStart, LocalDate releaseDateEnd, Pageable pageable) {

        // 장르를 제외한 영화 정보 가져오기
        List<MovieBaseResponse> baseResponseList = queryFactory
                .select(Projections.constructor(
                        MovieBaseResponse.class,
                        movie.id,
                        movie.title,
                        movie.director,
                        movie.releaseDate
                ))
                .from(movie)
                .leftJoin(movieGenre).on(movieGenre.movieId.eq(movie.id))  // 조건
                .leftJoin(genre).on(movieGenre.genreId.eq(genre.id))
                .where(
                        titleContains(title),
                        directorContains(director),
                        releaseDateBetween(releaseDateStart, releaseDateEnd),
                        genreContains(genreKeyword)
                )
                .distinct()  // 중복 조회 방지
                .orderBy(movie.id.desc())  // 이름 기준 조회로 수정 고려해보기➕
                .offset(pageable.getOffset())  // page * size 개수만큼 건너뛰기
                .limit(pageable.getPageSize())
                .fetch();


        // 전체 개수 조회(Page)
        Long totalCount = queryFactory
                .select(movie.id.countDistinct())
                .from(movie)
                .leftJoin(movieGenre).on(movieGenre.movieId.eq(movie.id))
                .leftJoin(genre).on(movieGenre.genreId.eq(genre.id))
                .where(
                        titleContains(title),
                        directorContains(director),
                        releaseDateBetween(releaseDateStart, releaseDateEnd),
                        genreContains(genreKeyword)
                )
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;  // 조회결과 null인 경우 방지 → 0으로


        // 영화 Id 리스트로 한 번에 가져오기(장르 한 번에 가져오기 위해서)
        List<Long> movieIds = new ArrayList<>();
        for (MovieBaseResponse base : baseResponseList) {
            Long id = base.getId();
            if (id != null) {
                movieIds.add(id);
            }
        }

        // movieIds 비어있는 경우(검색결과 없는 경우) 바로 반환
        if (movieIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total);
        }


        // 장르 조회
        List<MovieGenreResponse> genreResponseList = queryFactory
                .select(Projections.constructor(
                        MovieGenreResponse.class,
                        movieGenre.movieId,
                        genre.name
                ))
                .from(movieGenre)
                .join(genre).on(movieGenre.genreId.eq(genre.id))
                .where(movieGenre.movieId.in(movieIds))  // N + 1 문제 방지하기 위해 movieIds로 한 번에 조회
                .fetch();


        // movieId 기준으로 장르 가져오기
        Map<Long, List<String>> genreMap = new HashMap<>();
        for (MovieGenreResponse genreResponse : genreResponseList) {
            Long movieId = genreResponse.getMovieId();
            String genreName = genreResponse.getGenreName();

            if(movieId == null || genreName == null) continue;

            // movieId에 장르 할당하기
            List<String> genreList = genreMap.get(movieId);
            if (genreList == null) {
                genreList = new ArrayList<>();
                genreMap.put(movieId, genreList);  // List null일 경우 등록
            }
            genreList.add(genreName);
        }


        // 최종 ResponseDto
        List<MovieSearchResponse> results = new ArrayList<>();
        for(MovieBaseResponse base : baseResponseList) {
            Long movieId = base.getId();

            List<String> genres = genreMap.get(movieId);
            if (genres == null) genres = List.of();

            results.add(new MovieSearchResponse(
                    base.getId(),
                    base.getTitle(),
                    genres,
                    base.getDirector(),
                    base.getReleaseDate()
            ));
        }

        return new PageImpl<>(results, pageable, total);
    }


    // 동적 쿼리
    // 제목 기준 검색
    private BooleanExpression titleContains(String title) {
        // null 또는 공백이면 조건 미적용
        if (title == null || title.isBlank()) return null;
        return movie.title.containsIgnoreCase(title);
    }

    // 감독 기준 검색
    private BooleanExpression directorContains(String director) {
        if (director == null || director.isBlank()) return null;
        return movie.director.containsIgnoreCase(director);
    }

    // 장르 기준 검색
    private BooleanExpression genreContains(String genreKeyword) {
        if (genreKeyword == null || genreKeyword.isBlank()) return null;
        return genre.name.containsIgnoreCase(genreKeyword);
    }

    // 개봉일 기준 검색
    private BooleanExpression releaseDateBetween(LocalDate releaseDateStart, LocalDate releaseDateEnd) {
        if (releaseDateStart != null && releaseDateEnd != null)
            return movie.releaseDate.between(releaseDateStart, releaseDateEnd);
        if (releaseDateStart != null)
            return movie.releaseDate.after(releaseDateStart);
        if (releaseDateEnd != null)
            return movie.releaseDate.before(releaseDateEnd);
        else{
            return null;
        }
    }
}