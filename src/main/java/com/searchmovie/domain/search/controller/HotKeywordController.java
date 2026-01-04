package com.searchmovie.domain.search.controller;

import com.searchmovie.common.exception.SearchException;
import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.domain.search.model.GenreKeywordResponse;
import com.searchmovie.domain.search.model.HotKeywordResponse;
import com.searchmovie.domain.search.model.PeriodSearchResponse;
import com.searchmovie.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.searchmovie.common.enums.ExceptionCode.INVALID_MONTH;
import static com.searchmovie.common.enums.ExceptionCode.INVALID_SEARCH_PERIOD;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies/hot-keywords")
public class HotKeywordController {

    private final SearchService searchService;


    /**
     * 종합 인기검색어 TOP 10
     */
    @GetMapping("/synthesis")
    public ResponseEntity<CommonResponse<List<HotKeywordResponse>>> synthesis() {
        List<HotKeywordResponse> response = searchService.topOverall();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "종합 인기 검색어 조회 성공", response));
    }


    /**
     * 장르별 인기검색어 TOP 10
     */
    @GetMapping("/genre")
    public ResponseEntity<CommonResponse<List<GenreKeywordResponse>>> genre() {
        List<GenreKeywordResponse> response = searchService.topGenre();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "장르별 인기 검색어 조회 성공", response));
    }


    /**
     * 월간 인기검색어 TOP 10
     */
    @GetMapping("/period")
    public ResponseEntity<CommonResponse<PeriodSearchResponse>> period(@RequestParam(required = false) Integer year,
                                                                       @RequestParam(required = false) Integer month) {
        if (year != null && (year < 1900 || year > 2100)) {
            throw new SearchException(INVALID_SEARCH_PERIOD);
        }
        if (month != null && (month < 1 || month > 12)) {
            throw new SearchException(INVALID_MONTH);
        }
        PeriodSearchResponse response = searchService.topPeriod(year, month);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "월간 인기 검색어 조회 성공", response));
    }

}
