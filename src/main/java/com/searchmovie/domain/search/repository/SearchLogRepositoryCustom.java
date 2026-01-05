package com.searchmovie.domain.search.repository;

import com.searchmovie.domain.search.model.response.GenreKeywordResponse;
import com.searchmovie.domain.search.model.response.HotKeywordResponse;
import com.searchmovie.domain.search.model.response.PeriodKeywordResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchLogRepositoryCustom {

    List<HotKeywordResponse> findTopKeywords();
    List<GenreKeywordResponse> findTopGenres();
    List<PeriodKeywordResponse> findTopPeriod(LocalDateTime from, LocalDateTime to);
}