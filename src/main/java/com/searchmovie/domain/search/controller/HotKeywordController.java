package com.searchmovie.domain.search.controller;

import com.searchmovie.common.exception.SearchException;
import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.domain.search.model.response.GenreKeywordResponse;
import com.searchmovie.domain.search.model.response.HotKeywordResponse;
import com.searchmovie.domain.search.model.request.PeriodSearchRequest;
import com.searchmovie.domain.search.model.response.PeriodSearchResponse;
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
@RequestMapping("/api")
public class HotKeywordController {

    private final SearchService searchService;


    /**
     * 종합 인기검색어 TOP 10 (V1 - 캐시)
     */
    @GetMapping("/v1/movies/hot-keywords/synthesis")
    public ResponseEntity<CommonResponse<List<HotKeywordResponse>>> v1synthesis() {
        List<HotKeywordResponse> response = searchService.v1topOverall();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "종합 인기 검색어 조회 성공", response));
    }


    /**
     * 장르별 인기검색어 TOP 10 (V1 - 캐시)
     */
    @GetMapping("/v1/movies/hot-keywords/genre")
    public ResponseEntity<CommonResponse<List<GenreKeywordResponse>>> v1genre() {
        List<GenreKeywordResponse> response = searchService.v1topGenre();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "장르별 인기 검색어 조회 성공", response));
    }


    /**
     * 월간 인기검색어 TOP 10 (V1 - 캐시)
     */
    @GetMapping("/v1/movies/hot-keywords/period")
    public ResponseEntity<CommonResponse<PeriodSearchResponse>> v1period(@RequestParam(required = false) Integer year,
                                                                       @RequestParam(required = false) Integer month) {
        if (year != null && (year < 1900 || year > 2100)) {
            throw new SearchException(INVALID_SEARCH_PERIOD);
        }
        if (month != null && (month < 1 || month > 12)) {
            throw new SearchException(INVALID_MONTH);
        }

        PeriodSearchRequest periodSearchRequest = new PeriodSearchRequest(year, month);
        PeriodSearchResponse response = searchService.v1topPeriod(periodSearchRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "월간 인기 검색어 조회 성공", response));
    }


    // ==================================================
    // V2 (캐시 적용)
    // ==================================================

    /**
     * 종합 인기검색어 TOP 10 (V2 - 캐시)
     */
    @GetMapping("/v2/movies/hot-keywords/synthesis")
    public ResponseEntity<CommonResponse<List<HotKeywordResponse>>> v2synthesis() {
        List<HotKeywordResponse> response = searchService.v2topSynthesis();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "종합 인기 검색어 조회 성공", response));
    }

    /**
     * 장르별 인기검색어 TOP 10 (V2 - 캐시)
     */
    @GetMapping("/v2/movies/hot-keywords/genre")
    public ResponseEntity<CommonResponse<List<GenreKeywordResponse>>> v2genre() {
        List<GenreKeywordResponse> response = searchService.v2topGenre();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "장르별 인기 검색어 조회 성공", response));
    }

    /**
     * 월간 인기검색어 TOP 10 (V2 - 캐시)
     */
    @GetMapping("/v2/movies/hot-keywords/period")
    public ResponseEntity<CommonResponse<PeriodSearchResponse>> v2period(@RequestParam(required = false) Integer year,
                                                                         @RequestParam(required = false) Integer month) {
        if (year != null && (year < 1900 || year > 2100)) {
            throw new SearchException(INVALID_SEARCH_PERIOD);
        }
        if (month != null && (month < 1 || month > 12)) {
            throw new SearchException(INVALID_MONTH);
        }

        PeriodSearchRequest periodSearchRequest = new PeriodSearchRequest(year, month);
        PeriodSearchResponse response = searchService.v2topPeriod(periodSearchRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "월간 인기 검색어 조회 성공", response));
    }


    // ==================================================
    // V3 (스프링 캐시 + Redis)
    // ==================================================

//    /**
//     * 종합 인기검색어 TOP 10 (V2 - 캐시)
//     */
//    @GetMapping("/v2/movies/hot-keywords/synthesis")
//    public ResponseEntity<CommonResponse<List<HotKeywordResponse>>> v3synthesis() {
//        List<HotKeywordResponse> response = searchService.v3topSynthesis();
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(new CommonResponse<>(true, "종합 인기 검색어 조회 성공", response));
//    }
//
//    /**
//     * 장르별 인기검색어 TOP 10 (V2 - 캐시)
//     */
//    @GetMapping("/v2/movies/hot-keywords/genre")
//    public ResponseEntity<CommonResponse<List<GenreKeywordResponse>>> v3genre() {
//        List<GenreKeywordResponse> response = searchService.v3topGenre();
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(new CommonResponse<>(true, "장르별 인기 검색어 조회 성공", response));
//    }
//
//    /**
//     * 월간 인기검색어 TOP 10 (V2 - 캐시)
//     */
//    @GetMapping("/v2/movies/hot-keywords/period")
//    public ResponseEntity<CommonResponse<PeriodSearchResponse>> v3period(@RequestParam(required = false) Integer year,
//                                                                         @RequestParam(required = false) Integer month) {
//        if (year != null && (year < 1900 || year > 2100)) {
//            throw new SearchException(INVALID_SEARCH_PERIOD);
//        }
//        if (month != null && (month < 1 || month > 12)) {
//            throw new SearchException(INVALID_MONTH);
//        }
//
//        PeriodSearchRequest periodSearchRequest = new PeriodSearchRequest(year, month);
//        PeriodSearchResponse response = searchService.v3topPeriod(periodSearchRequest);
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(new CommonResponse<>(true, "월간 인기 검색어 조회 성공", response));
//    }
}
