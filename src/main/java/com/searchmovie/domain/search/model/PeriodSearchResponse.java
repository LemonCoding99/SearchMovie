package com.searchmovie.domain.search.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PeriodSearchResponse {

    private final String yearMonth;
    private final List<PeriodKeywordResponse> periodKeyword;
}
