package com.searchmovie.domain.search.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.searchmovie.domain.search.dto.*;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.searchmovie.domain.movie.entity.QGenre.genre;
import static com.searchmovie.domain.search.entity.QSearchLog.searchLog;
import static com.searchmovie.domain.movie.entity.QMovie.movie;

@RequiredArgsConstructor
public class SearchLogRepositoryImpl implements SearchLogRepositoryCustom {

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
        List<SearchKeywordResponse> rows = queryFactory
                .select(Projections.constructor(SearchKeywordResponse.class,
                        searchLog.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate,
                        searchLog.count.sum()))
                .from(searchLog)
                .leftJoin(movie).on(movie.id.eq(searchLog.movieId))
                .leftJoin(genre).on(genre.id.eq(searchLog.genreId))
                .where(searchLog.keyword.isNotNull(),
                        searchLog.keyword.ne("")
                )
                .groupBy(searchLog.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate)
                .orderBy(searchLog.count.sum().desc())
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
        List<SearchGenresResponse> rows = queryFactory
                .select(Projections.constructor(SearchGenresResponse.class,
                        genre.name,
                        searchLog.count.sum()))
                .from(searchLog)
                .leftJoin(genre).on(genre.id.eq(searchLog.genreId))
                .where(searchLog.genreId.isNotNull())
                .groupBy(genre.name)
                .orderBy(searchLog.count.sum().desc())
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
        List<SearchPeriodResponse> rows = queryFactory
                .select(Projections.constructor(SearchPeriodResponse.class,
                        searchLog.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate,
                        searchLog.count.sum()))
                .from(searchLog)
                .leftJoin(movie).on(movie.id.eq(searchLog.movieId))
                .leftJoin(genre).on(genre.id.eq(searchLog.genreId))
//                .leftJoin(searchLog.movie, movie)
//                .leftJoin(searchLog.genre, genre)
                .where( searchLog.keyword.isNotNull(),
                        searchLog.keyword.ne(""),
                        searchedBetween(from, to)
                )
                .groupBy(searchLog.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate
                )
                .orderBy(searchLog.count.sum().desc())
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

