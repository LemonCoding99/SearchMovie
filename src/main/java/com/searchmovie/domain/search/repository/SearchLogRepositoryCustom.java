package com.searchmovie.domain.search.repository;

import com.searchmovie.domain.search.dto.GenreKeywordResponse;
import com.searchmovie.domain.search.dto.HotKeywordResponse;
import com.searchmovie.domain.search.dto.PeriodKeywordResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchLogRepositoryCustom {

    List<HotKeywordResponse> findTopKeywords();
    List<GenreKeywordResponse> findTopGenres();
    List<PeriodKeywordResponse> findTopPeriod(LocalDateTime from, LocalDateTime to);
}