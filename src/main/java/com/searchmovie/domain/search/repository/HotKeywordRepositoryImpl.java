package com.searchmovie.domain.search.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.searchmovie.domain.search.model.response.*;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.searchmovie.domain.movie.entity.QGenre.genre;
import static com.searchmovie.domain.movie.entity.QMovie.movie;
import static com.searchmovie.domain.movie.entity.QMovieGenre.movieGenre;
import static com.searchmovie.domain.search.entity.QHotKeyword.hotKeyword;

@RequiredArgsConstructor
public class HotKeywordRepositoryImpl implements HotKeywordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 기간별 조회를 위한 검색 조건 생성 (Querydsl BooleanExpression)
    private BooleanExpression searchedBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) return null;
        if (from == null) return hotKeyword.searchedAt.lt(to);
        if (to == null) return hotKeyword.searchedAt.goe(from);
        return hotKeyword.searchedAt.goe(from).and(hotKeyword.searchedAt.lt(to));
    }

    //종합 인기검색어 TOP 10
    @Override
    public List<SynthesisRankResponse> fetchSynthesisTop10() {

        NumberExpression<Long> score = hotKeyword.id.count();

        List<RepositoryInSynthesisDto> rows = queryFactory
                .select(Projections.constructor(
                        RepositoryInSynthesisDto.class,
                        hotKeyword.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate,
                        score
                ))
                .from(hotKeyword)
                .leftJoin(movie).on(movie.id.eq(hotKeyword.movieId))
                .leftJoin(movieGenre).on(movieGenre.movieId.eq(movie.id))
                .leftJoin(genre).on(genre.id.eq(movieGenre.genreId))
                .where(hotKeyword.keyword.isNotNull(),
                        hotKeyword.keyword.ne(""))
                .groupBy(hotKeyword.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate)
                .orderBy(score.desc())
                .limit(10)
                .fetch();

        return IntStream.range(0, rows.size())
                .mapToObj(i -> new SynthesisRankResponse(
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

    //장르별 인기검색어 TOP 10
    @Override
    public List<GenreRankResponse> fetchGenreTop10() {

        NumberExpression<Long> score = hotKeyword.id.count();

        List<RepositoryInGenresDto> rows = queryFactory
                .select(Projections.constructor(
                        RepositoryInGenresDto.class,
                        genre.name,
                        score
                ))
                .from(hotKeyword)
                .leftJoin(movie).on(movie.id.eq(hotKeyword.movieId))
                .leftJoin(movieGenre).on(movieGenre.movieId.eq(movie.id))
                .leftJoin(genre).on(genre.id.eq(movieGenre.genreId))
                .groupBy(genre.name)
                .orderBy(score.desc())
                .limit(10)
                .fetch();

        return IntStream.range(0, rows.size())
                .mapToObj(i -> new GenreRankResponse(
                        i + 1,
                        rows.get(i).getGenre(),
                        rows.get(i).getScore()
                ))
                .toList();
    }

    //월간 인기검색어 TOP 10
    @Override
    public List<PeriodRankResponse> fetchPeriodTop10(LocalDateTime from, LocalDateTime to) {

        NumberExpression<Long> score = hotKeyword.id.count();

        List<RepositoryInPeriodDto> rows = queryFactory
                .select(Projections.constructor(
                        RepositoryInPeriodDto.class,
                        hotKeyword.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate,
                        score
                ))
                .from(hotKeyword)
                .leftJoin(movie).on(movie.id.eq(hotKeyword.movieId))
                .leftJoin(movieGenre).on(movieGenre.movieId.eq(movie.id))
                .leftJoin(genre).on(genre.id.eq(movieGenre.genreId))
                .where(hotKeyword.keyword.isNotNull(),
                        hotKeyword.keyword.ne(""),
                        searchedBetween(from, to)
                )
                .groupBy(hotKeyword.keyword,
                        movie.title,
                        genre.name,
                        movie.director,
                        movie.releaseDate
                )
                .orderBy(score.desc())
                .limit(10)
                .fetch();

        return IntStream.range(0, rows.size())
                .mapToObj(i -> new PeriodRankResponse(
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