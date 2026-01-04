package com.searchmovie.domain.search.repository;

import com.searchmovie.domain.search.model.GenreKeywordResponse;
import com.searchmovie.domain.search.model.HotKeywordResponse;
import com.searchmovie.domain.search.model.PeriodKeywordResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchLogRepositoryCustom {

    List<HotKeywordResponse> findTopKeywords();
    List<GenreKeywordResponse> findTopGenres();
    List<PeriodKeywordResponse> findTopPeriod(LocalDateTime from, LocalDateTime to);
}