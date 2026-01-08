package com.searchmovie.domain.search.repository;

import com.searchmovie.domain.search.model.response.GenreRankResponse;
import com.searchmovie.domain.search.model.response.SynthesisRankResponse;
import com.searchmovie.domain.search.model.response.PeriodRankResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface HotKeywordRepositoryCustom {

    List<SynthesisRankResponse> fetchSynthesisTop10();

    List<GenreRankResponse> fetchGenreTop10();

    List<PeriodRankResponse> fetchPeriodTop10(LocalDateTime from, LocalDateTime to);
}