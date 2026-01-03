package com.searchmovie.domain.search.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.domain.search.dto.GenreKeywordResponse;
import com.searchmovie.domain.search.dto.HotKeywordResponse;
import com.searchmovie.domain.search.dto.PeriodKeywordResponse;
import com.searchmovie.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies/hot-keywords")
public class HotKeywordController {

    private final SearchService searchService;
    private LocalDate from;
    private LocalDate localDate;

    /**
     * 종합 인기검색어 TOP 10
     */
    @GetMapping("/synthesis")
    public CommonResponse<List<HotKeywordResponse>> synthesis() {
        List<HotKeywordResponse> response = searchService.topOverall();
        return new CommonResponse(true, "종합 인기 검색어 조회 성공", response);
    }


    /**
     * 장르별 인기검색어 TOP 10
     */
    @GetMapping("/genre")
    public CommonResponse<List<GenreKeywordResponse>> genre() {
        List<GenreKeywordResponse> response = searchService.topGenre();
        return new CommonResponse(true, "장르별 인기 검색어 조회 성공", response);
    }


    /**
     * 월간 인기검색어 TOP 10
     */
    @GetMapping("/period")
    public CommonResponse<List<PeriodKeywordResponse>> period(@RequestParam(required = false) Integer year,
                                                              @RequestParam(required = false) Integer month) {
        List<PeriodKeywordResponse> response = searchService.topPeriod(year, month);
        return new CommonResponse(true, "월간 인기 검색어 조회 성공", response);
    }

    /**
     * 리뷰점수 인기검색어 TOP 10
     */
}
