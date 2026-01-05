package com.searchmovie.domain.search.service;

import com.searchmovie.domain.search.model.request.PeriodSearchRequest;
import com.searchmovie.domain.search.model.response.GenreKeywordResponse;
import com.searchmovie.domain.search.model.response.HotKeywordResponse;
import com.searchmovie.domain.search.model.response.PeriodKeywordResponse;
import com.searchmovie.domain.search.model.response.PeriodSearchResponse;
import com.searchmovie.domain.search.repository.SearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;

    /**
     * 종합 인기검색어 TOP 10
     */
    @Transactional
    public List<HotKeywordResponse> v1topOverall() {
        return searchRepository.findTopKeywords();
    }


    /**
     * 장르별 인기검색어 TOP 10
     */
    @Transactional
    public List<GenreKeywordResponse> v1topGenre() {
        return searchRepository.findTopGenres();
    }


    /**
     * 월간 인기검색어 TOP 10
     */
    @Transactional
    public PeriodSearchResponse v1topPeriod(PeriodSearchRequest request) {

        YearMonth yearMonth = YearMonth.of(request.getYear(), request.getMonth());

        LocalDateTime from = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime to = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<PeriodKeywordResponse> periodKeywords = searchRepository.findTopPeriod(from, to);

        return new PeriodSearchResponse(
                yearMonth.toString(),
                periodKeywords);
    }

    // =========================
    // V2 (캐시 적용)
    // =========================

    @Transactional(readOnly = true)
    @Cacheable(value = "searchMovie", key = "'overall'", sync = true)
    public List<HotKeywordResponse> v2topSynthesis() {
        log.info("[CACHE_MISS] v2topSynthesis-> key=overall (method executed, DB will be hit)");
        return searchRepository.findTopKeywords();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "searchMovie", key = "'genre'", sync = true)
    public List<GenreKeywordResponse> v2topGenre() {
        log.info("[CACHE_MISS] v2topGenre -> key=genre (method executed, DB will be hit)");
        return searchRepository.findTopGenres();
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "searchMovie",
            key = "'period:' + #request.toYear() + '-' + #request.toMonth()",
            sync = true
    )
    public PeriodSearchResponse v2topPeriod(PeriodSearchRequest request) {
        log.info("[CACHE_MISS] v2topPeriod -> key=period:{}-{} (method executed, DB will be hit)", request.toYear(), request.toMonth());
        YearMonth yearMonth = YearMonth.of(request.getYear(), request.getMonth());

        LocalDateTime from = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime to = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<PeriodKeywordResponse> periodKeywords = searchRepository.findTopPeriod(from, to);
        return new PeriodSearchResponse(yearMonth.toString(), periodKeywords);
    }
}
