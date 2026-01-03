package com.searchmovie.domain.search.service;


import com.searchmovie.domain.search.dto.GenreKeywordResponse;
import com.searchmovie.domain.search.dto.HotKeywordResponse;
import com.searchmovie.domain.search.dto.PeriodKeywordResponse;
import com.searchmovie.domain.search.dto.PeriodSearchResponse;
import com.searchmovie.domain.search.repository.SearchLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchLogRepository searchLogRepository;

    /**
     * 종합 인기검색어 TOP 10
     */
    @Transactional
    public List<HotKeywordResponse> topOverall() {
        return searchLogRepository.findTopKeywords();
    }


    /**
     * 장르별 인기검색어 TOP 10
     */
    @Transactional
    public List<GenreKeywordResponse> topGenre() {
        return searchLogRepository.findTopGenres();
    }


    /**
     * 월간 인기검색어 TOP 10
     */
    @Transactional
    public PeriodSearchResponse topPeriod(Integer year, Integer month) {

        YearMonth yearMonth = (year == null || month == null)
                ? YearMonth.now()
                : YearMonth.of(year, month);

        LocalDateTime from = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime to = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<PeriodKeywordResponse> periodKeywords = searchLogRepository.findTopPeriod(from, to);

        return new PeriodSearchResponse(
                yearMonth.toString(),
                periodKeywords);
    }

}
