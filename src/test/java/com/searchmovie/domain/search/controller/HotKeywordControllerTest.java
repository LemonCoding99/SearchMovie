package com.searchmovie.domain.search.controller;

import com.searchmovie.common.exception.GlobalExceptionHandler;
import com.searchmovie.common.filter.JwtFilter;
import com.searchmovie.domain.search.model.response.HotKeywordResponse;
import com.searchmovie.domain.search.model.response.PeriodKeywordResponse;
import com.searchmovie.domain.search.model.request.PeriodSearchRequest;
import com.searchmovie.domain.search.model.response.PeriodSearchResponse;
import com.searchmovie.domain.search.service.SearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(HotKeywordController.class)
@Import(GlobalExceptionHandler.class)
class HotKeywordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    @DisplayName("종합 인기 검색어 TOP 10 조회")
    void testSynthesis() throws Exception {
        List<HotKeywordResponse> serviceResponse = List.of(
                new HotKeywordResponse(1, "고질라", "고질라", "로맨스", "하륜 정",
                        LocalDate.of(2014, 11, 7), 22L),
                new HotKeywordResponse(2, "라푼젤", "긴생머리 그녀", "호러", "순관 정",
                        LocalDate.of(2010, 7, 16), 11L)
        );
        given(searchService.v1topOverall()).willReturn(serviceResponse);

        mockMvc.perform(get("/api/v1/movies/hot-keywords/synthesis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("종합 인기 검색어 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].rank").value(1))
                .andExpect(jsonPath("$.data[0].keyword").value("고질라"))
                .andExpect(jsonPath("$.data[0].score").value(22));
    }


    @Test
    @DisplayName("월간 인기 검색어 TOP 10 조회")
    void testPeriod() throws Exception {

        PeriodSearchRequest periodSearchRequest = new PeriodSearchRequest(2025, 1);

        PeriodSearchResponse serviceResponse = new PeriodSearchResponse(
                "2025-01",
                List.of(
                        new PeriodKeywordResponse(1, "고질라", "고질라", "로맨스",
                                "하륜 정", LocalDate.of(2014, 11, 7), 22L),
                        new PeriodKeywordResponse(2, "라푼젤", "긴생머리 그녀", "호러", "순관 정",
                                LocalDate.of(2010, 7, 16), 11L)
                )
        );

        given(searchService.v1topPeriod(any(PeriodSearchRequest.class)))
                .willReturn(serviceResponse);

        mockMvc.perform(get("/api/v1/movies/hot-keywords/period")
                        .param("year", "2025")
                        .param("month", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("월간 인기 검색어 조회 성공"))
                .andExpect(jsonPath("$.data.yearMonth").value("2025-01"))
                .andExpect(jsonPath("$.data.periodKeyword[0].rank").value(1))
                .andExpect(jsonPath("$.data.periodKeyword[0].score").value(22));
    }

    @Test
    @DisplayName("월간 인기 검색어 - month 범위가 잘못되면 400")
    void testPeriod_invalidMonth_400() throws Exception {

        given(searchService.v1topPeriod(any(PeriodSearchRequest.class)))
                .willThrow(new IllegalArgumentException("검색 월(month)은 1~12 사이여야 합니다."));

        mockMvc.perform(get("/api/v1/movies/hot-keywords/period")
                        .param("year", "2025")
                        .param("month", "13"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("검색 월(month)은 1~12 사이여야 합니다."));
    }
}