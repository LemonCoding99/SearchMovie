package com.searchmovie.domain.search.controller;

import com.searchmovie.common.exception.CustomException;
import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.domain.search.model.request.PeriodRankRequest;
import com.searchmovie.domain.search.model.response.GenreRankResponse;
import com.searchmovie.domain.search.model.response.PeriodRankListResponse;
import com.searchmovie.domain.search.model.response.SynthesisRankResponse;
import com.searchmovie.domain.search.service.HotKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static com.searchmovie.common.enums.ExceptionCode.INVALID_MONTH;
import static com.searchmovie.common.enums.ExceptionCode.INVALID_SEARCH_PERIOD;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HotKeywordController {

    private final HotKeywordService hotKeywordService;

    // ==================================================
    // V1 (캐싱 미적용)
    // ==================================================

    //종합 인기검색어 TOP 10
    @GetMapping("/v1/movies/hot-keywords/synthesis")
    public ResponseEntity<CommonResponse<List<SynthesisRankResponse>>> v1synthesis() {
        List<SynthesisRankResponse> response = hotKeywordService.v1SynthesisRanking();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "종합 인기 검색어 조회 성공", response));
    }


    //장르별 인기검색어 TOP 10
    @GetMapping("/v1/movies/hot-keywords/genre")
    public ResponseEntity<CommonResponse<List<GenreRankResponse>>> v1genre() {
        List<GenreRankResponse> response = hotKeywordService.v1GenreRanking();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "장르별 인기 검색어 조회 성공", response));
    }

    //월간 인기검색어 TOP 10
    @GetMapping("/v1/movies/hot-keywords/period")
    public ResponseEntity<CommonResponse<PeriodRankListResponse>> v1Period(@RequestParam(required = false) Integer year,
                                                                           @RequestParam(required = false) Integer month) {
        int resolvedYear = (year == null) ? LocalDate.now().getYear() : year;
        int resolvedMonth = (month == null) ? LocalDate.now().getMonthValue() : month;

        if (resolvedYear < 1900 || resolvedYear > 2100) {
            throw new CustomException(INVALID_SEARCH_PERIOD);
        }
        if (resolvedMonth < 1 || resolvedMonth > 12) {
            throw new CustomException(INVALID_MONTH);
        }

        PeriodRankRequest periodRankRequest = new PeriodRankRequest(resolvedYear, resolvedMonth);
        PeriodRankListResponse response = hotKeywordService.v1PeriodRanking(periodRankRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "월간 인기 검색어 조회 성공", response));
    }

    // ==================================================
    // V2 (Spring 캐시 적용)
    // ==================================================

    //종합 인기검색어 TOP 10
    @GetMapping("/v2/movies/hot-keywords/synthesis")
    public ResponseEntity<CommonResponse<List<SynthesisRankResponse>>> v2synthesis() {
        List<SynthesisRankResponse> response = hotKeywordService.v2SynthesisRanking();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "종합 인기 검색어 조회 성공", response));
    }

    //장르별 인기검색어 TOP 10
    @GetMapping("/v2/movies/hot-keywords/genre")
    public ResponseEntity<CommonResponse<List<GenreRankResponse>>> v2genre() {
        List<GenreRankResponse> response = hotKeywordService.v2GenreRanking();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "장르별 인기 검색어 조회 성공", response));
    }

    //월간 인기검색어 TOP 10
    @GetMapping("/v2/movies/hot-keywords/period")
    public ResponseEntity<CommonResponse<PeriodRankListResponse>> v2period(@RequestParam(required = false) Integer year,
                                                                           @RequestParam(required = false) Integer month) {
        int resolvedYear = (year == null) ? LocalDate.now().getYear() : year;
        int resolvedMonth = (month == null) ? LocalDate.now().getMonthValue() : month;

        if (resolvedYear < 2017 || resolvedYear > 2020) {
            throw new CustomException(INVALID_SEARCH_PERIOD);
        }
        if (resolvedMonth < 1 || resolvedMonth > 12) {
            throw new CustomException(INVALID_MONTH);
        }

        PeriodRankRequest periodRankRequest = new PeriodRankRequest(resolvedYear, resolvedMonth);
        PeriodRankListResponse response = hotKeywordService.v2PeriodRanking(periodRankRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "월간 인기 검색어 조회 성공", response));
    }


    // ==================================================
    // V3 (Redis 캐시 적용)
    // ==================================================

    //종합 인기검색어 TOP 10
    @GetMapping("/v3/movies/hot-keywords/synthesis")
    public ResponseEntity<CommonResponse<List<SynthesisRankResponse>>> v3synthesis() {
        List<SynthesisRankResponse> response = hotKeywordService.v3SynthesisRanking();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "종합 인기 검색어 조회 성공", response));
    }

    //장르별 인기검색어 TOP 10
    @GetMapping("/v3/movies/hot-keywords/genre")
    public ResponseEntity<CommonResponse<List<GenreRankResponse>>> v3genre() {
        List<GenreRankResponse> response = hotKeywordService.v3GenreRanking();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "장르별 인기 검색어 조회 성공", response));
    }

    //월간 인기검색어 TOP 10
    @GetMapping("/v3/movies/hot-keywords/period")
    public ResponseEntity<CommonResponse<PeriodRankListResponse>> v3period(@RequestParam(required = false) Integer year,
                                                                           @RequestParam(required = false) Integer month) {
        int resolvedYear = (year == null) ? LocalDate.now().getYear() : year;
        int resolvedMonth = (month == null) ? LocalDate.now().getMonthValue() : month;

        if (resolvedYear < 1900 || resolvedYear > 2100) {
            throw new CustomException(INVALID_SEARCH_PERIOD);
        }
        if (resolvedMonth < 1 || resolvedMonth > 12) {
            throw new CustomException(INVALID_MONTH);
        }

        PeriodRankRequest periodRankRequest = new PeriodRankRequest(resolvedYear, resolvedMonth);
        PeriodRankListResponse response = hotKeywordService.v3PeriodRanking(periodRankRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommonResponse<>(true, "월간 인기 검색어 조회 성공", response));
    }
}
