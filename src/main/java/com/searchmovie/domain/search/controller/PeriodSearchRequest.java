package com.searchmovie.domain.search.controller;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PeriodSearchRequest {

    @NotNull(message = "검색 연도(year)는 필수입니다.")
    @Min(value = 1900, message = "검색 연도(year)는 4자리여야 합니다.")
    @Max(value = 9999, message = "검색 연도(year)는 4자리여야 합니다.")
    private Integer year;

    @NotNull(message = "검색 월(month)은 필수 입니다.")
    @Min(value = 1, message = "검색 월(month)은 1~12 사이여야 합니다.")
    @Max(value = 12, message = "검색 월(month)은 1~12 사이여야 합니다.")
    private Integer month;

    public String toYear() {
        return String.format("%04d", this.year);
    }

    public String toMonth() {
        return String.format("%02d", this.month);
    }
}
