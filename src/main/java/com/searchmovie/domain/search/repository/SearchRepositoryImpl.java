package com.searchmovie.domain.search.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.searchmovie.domain.search.model.*;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import static com.searchmovie.domain.movie.entity.QGenre.genre;
import static com.searchmovie.domain.movie.entity.QMovie.movie;
import static com.searchmovie.domain.movie.entity.QMovieGenre.movieGenre;
import static com.searchmovie.domain.search.entity.QSearchLog.searchLog;

@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchLogRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    //월간 인기검색 조건용
    private BooleanExpression searchedBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) return null;
        if (from == null) return searchLog.searchedAt.lt(to);
        if (to == null) return searchLog.searchedAt.goe(from);
        return searchLog.searchedAt.goe(from).and(searchLog.searchedAt.lt(to));
    }


    /**
     * 장르별 인기검색어 TOP 10
     */
    @Override
    public List<HotKeywordResponse> findTopKeywords() {

        NumberExpression<Long> score = searchLog.id.count();

        List<SearchKeywordResponse> rows = queryFactory
                .select(Projections.constructor(
                        SearchKeywordResponse.class,
                        searchLog.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate,
                        score
                ))
                .from(searchLog)
                .leftJoin(movie).on(movie.id.eq(searchLog.movieId))
                .leftJoin(movieGenre).on(movieGenre.movieId.eq(movie.id))
                .leftJoin(genre).on(genre.id.eq(movieGenre.genreId))
                .where(searchLog.keyword.isNotNull(),
                        searchLog.keyword.ne(""))
                .groupBy(searchLog.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate)
                .orderBy(score.desc())
                .limit(10)
                .fetch();

        return IntStream.range(0, rows.size())
                .mapToObj(i -> new HotKeywordResponse(
                        i + 1,
                        rows.get(i).getKeyword(),
                        rows.get(i).getTitle(),
                        rows.get(i).getGenre(),
                        rows.get(i).getDirector(),
                        rows.get(i).getReleaseDate(),
                        rows.get(i).getScore()
                ))
                .toList();
    }


    /**
     * 장르별 인기검색어 TOP 10
     */
    @Override
    public List<GenreKeywordResponse> findTopGenres() {

        NumberExpression<Long> score = searchLog.id.count();

        List<SearchGenresResponse> rows = queryFactory
                .select(Projections.constructor(
                        SearchGenresResponse.class,
                        genre.name,
                        score
                ))
                .from(searchLog)
                .leftJoin(movie).on(movie.id.eq(searchLog.movieId))
                .leftJoin(movieGenre).on(movieGenre.movieId.eq(movie.id))
                .leftJoin(genre).on(genre.id.eq(movieGenre.genreId))
                .groupBy(genre.name)
                .orderBy(score.desc())
                .limit(10)
                .fetch();

        return IntStream.range(0, rows.size())
                .mapToObj(i -> new GenreKeywordResponse(
                        i + 1,
                        rows.get(i).getGenre(),
                        rows.get(i).getScore()
                ))
                .toList();
    }


    /**
     * 월간 인기검색어 TOP 10
     */
    @Override
    public List<PeriodKeywordResponse> findTopPeriod(LocalDateTime from, LocalDateTime to) {

        NumberExpression<Long> score = searchLog.id.count();

        List<SearchPeriodResponse> rows = queryFactory
                .select(Projections.constructor(
                        SearchPeriodResponse.class,
                        searchLog.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate,
                        score
                ))
                .from(searchLog)
                .leftJoin(movie).on(movie.id.eq(searchLog.movieId))
                .leftJoin(movieGenre).on(movieGenre.movieId.eq(movie.id))
                .leftJoin(genre).on(genre.id.eq(movieGenre.genreId))
                .where(searchLog.keyword.isNotNull(),
                        searchLog.keyword.ne(""),
                        searchedBetween(from, to)
                )
                .groupBy(searchLog.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate
                )
                .orderBy(score.desc())
                .limit(10)
                .fetch();

        return IntStream.range(0, rows.size())
                .mapToObj(i -> new PeriodKeywordResponse(
                        i + 1,
                        rows.get(i).getKeyword(),
                        rows.get(i).getTitle(),
                        rows.get(i).getGenre(),
                        rows.get(i).getDirector(),
                        rows.get(i).getReleaseDate(),
                        rows.get(i).getScore()
                ))
                .toList();
    }

}

