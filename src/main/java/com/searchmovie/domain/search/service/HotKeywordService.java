package com.searchmovie.domain.search.service;

import com.searchmovie.domain.search.model.request.PeriodRankRequest;
import com.searchmovie.domain.search.model.response.GenreRankResponse;
import com.searchmovie.domain.search.model.response.PeriodRankListResponse;
import com.searchmovie.domain.search.model.response.PeriodRankResponse;
import com.searchmovie.domain.search.model.response.SynthesisRankResponse;
import com.searchmovie.domain.search.repository.HotKeywordRepository;
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
public class HotKeywordService {

    private final HotKeywordRepository hotKeywordRepository;
    private final HotKeywordCacheService hotKeywordCacheService;

    // ==================================================
    // V1 (캐싱 미적용)
    // ==================================================

    //종합 인기검색어 TOP 10
    @Transactional
    public List<SynthesisRankResponse> v1SynthesisRanking() {
        return hotKeywordRepository.fetchSynthesisTop10();
    }

    //장르별 인기검색어 TOP 10
    @Transactional
    public List<GenreRankResponse> v1GenreRanking() {
        return hotKeywordRepository.fetchGenreTop10();
    }

    //월간 인기검색어 TOP 10
    @Transactional
    public PeriodRankListResponse v1PeriodRanking(PeriodRankRequest request) {

        YearMonth yearMonth = YearMonth.of(request.getYear(), request.getMonth());

        LocalDateTime from = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime to = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<PeriodRankResponse> periodKeywords = hotKeywordRepository.fetchPeriodTop10(from, to);

        return new PeriodRankListResponse(
                yearMonth.toString(),
                periodKeywords);
    }

    // ==================================================
    // V2 (Spring 캐시 적용)
    // ==================================================

    //종합 인기검색어 TOP 10
    @Transactional(readOnly = true)
    @Cacheable(value = "searchMovie", key = "'v2SynthesisRanking'", sync = true)
    public List<SynthesisRankResponse> v2SynthesisRanking() {
        log.info("[CACHE_MISS] v2topSynthesis-> key=overall (DB hit)");
        return hotKeywordRepository.fetchSynthesisTop10();
    }

    //장르별 인기검색어 TOP 10
    @Transactional(readOnly = true)
    @Cacheable(value = "searchMovie", key = "'v2GenreRanking'", sync = true)
    public List<GenreRankResponse> v2GenreRanking() {
        log.info("[CACHE_MISS] v2topGenre -> genre (DB hit)");
        return hotKeywordRepository.fetchGenreTop10();
    }

    //월간 인기검색어 TOP 10
    @Transactional(readOnly = true)
    @Cacheable(
            value = "searchMovie",
            key = "'v2PeriodRanking:' + #request.toYear() + '-' + #request.toMonth()",
            sync = true
    )
    public PeriodRankListResponse v2PeriodRanking(PeriodRankRequest request) {
        log.info("[CACHE_MISS] v2topPeriod -> period:{}-{} (DB hit)", request.toYear(), request.toMonth());

        YearMonth yearMonth = YearMonth.of(request.getYear(), request.getMonth());
        LocalDateTime from = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime to = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<PeriodRankResponse> periodKeywords = hotKeywordRepository.fetchPeriodTop10(from, to);
        return new PeriodRankListResponse(yearMonth.toString(), periodKeywords);
    }

    // ==================================================
    // V3 (Redis 캐시 적용)
    // ==================================================

    //종합 인기검색어 TOP 10
    @Transactional(readOnly = true)
    public List<SynthesisRankResponse> v3SynthesisRanking() {
        List<SynthesisRankResponse> cached = hotKeywordCacheService.getSynthesis();
        if (cached != null) {
            return cached;
        }

        log.info("[CACHE_MISS] v3topSynthesis -> DB hit");
        List<SynthesisRankResponse> topKeywords = hotKeywordRepository.fetchSynthesisTop10();
        hotKeywordCacheService.saveSynthesis(topKeywords);
        return topKeywords;
    }

    //장르별 인기검색어 TOP 10
    @Transactional(readOnly = true)
    public List<GenreRankResponse> v3GenreRanking() {
        List<GenreRankResponse> cached = hotKeywordCacheService.getGenre();
        if (cached != null) {
            return cached;
        }

        log.info("[CACHE_MISS] v3topGenre -> DB hit");
        List<GenreRankResponse> topGenres = hotKeywordRepository.fetchGenreTop10();
        hotKeywordCacheService.saveGenre(topGenres);

        return topGenres;
    }

    //월간 인기검색어 TOP 10
    @Transactional(readOnly = true)
    public PeriodRankListResponse v3PeriodRanking(PeriodRankRequest request) {

        PeriodRankListResponse period = hotKeywordCacheService.getPeriod(request.getYear(), request.getMonth());
        if (period != null) {
            return period;
        }

        log.info("[CACHE_MISS] v3topPeriod -> period:{}-{} (DB hit)", request.toYear(), request.toMonth());
        YearMonth yearMonth = YearMonth.of(request.getYear(), request.getMonth());
        LocalDateTime from = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime to = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<PeriodRankResponse> periodKeywords = hotKeywordRepository.fetchPeriodTop10(from, to);
        PeriodRankListResponse response = new PeriodRankListResponse(yearMonth.toString(), periodKeywords);

        hotKeywordCacheService.savePeriod(request.getYear(), request.getMonth(), response);

        return response;
    }
}
