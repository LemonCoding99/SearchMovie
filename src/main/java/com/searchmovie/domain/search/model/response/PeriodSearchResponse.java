package com.searchmovie.domain.search.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public class PeriodSearchResponse {

    private final String yearMonth;
    private final List<PeriodKeywordResponse> periodKeyword;

    @JsonCreator
    public PeriodSearchResponse(
            @JsonProperty("yearMonth") String yearMonth,
            @JsonProperty("periodKeyword") List<PeriodKeywordResponse> periodKeyword) {
        this.yearMonth = yearMonth;
        this.periodKeyword = periodKeyword;
    }
}
