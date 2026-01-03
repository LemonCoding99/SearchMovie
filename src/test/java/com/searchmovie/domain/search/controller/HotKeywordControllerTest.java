package com.searchmovie.domain.search.controller;

import com.searchmovie.domain.search.dto.HotKeywordResponse;
import com.searchmovie.domain.search.dto.PeriodKeywordResponse;
import com.searchmovie.domain.search.dto.PeriodSearchResponse;
import com.searchmovie.domain.search.service.SearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(HotKeywordController.class)
class HotKeywordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @Test
    @DisplayName("종합 인기 검색어 TOP 10 조회")
    void testSynthesis() throws Exception {

        //given
        List<HotKeywordResponse> serviceResponse = List.of(
                new HotKeywordResponse(1, "고질라", "고질라", "로맨스", "하륜 정",
                        LocalDate.of(2014, 11, 7), 22L),
                new HotKeywordResponse(2, "라푼젤", "긴생머리 그녀", "호러", "순관 정",
                        LocalDate.of(2010, 7, 16), 11L)
        );
        given(searchService.topOverall()).willReturn(serviceResponse);
        //when&then
        mockMvc.perform(get("/api/v1/movies/hot-keywords/synthesis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("종합 인기 검색어 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].rank").value(1))
                .andExpect(jsonPath("$.data[0].keyword").value("고질라"))
                .andExpect(jsonPath("$.data[0].title").value("고질라"))
                .andExpect(jsonPath("$.data[0].genre").value("로맨스"))
                .andExpect(jsonPath("$.data[0].director").value("하륜 정"))
                .andExpect(jsonPath("$.data[0].releaseDate").value("2014-11-07"))
                .andExpect(jsonPath("$.data[0].score").value(22));
    }

    @Test
    @DisplayName("월간 인기 검색어 TOP 10 조회")
    void testPeriod() throws Exception {
        // given
        PeriodSearchResponse serviceResponse = new PeriodSearchResponse(
                "2025-01",
                List.of(
                        new PeriodKeywordResponse(1,
                                "Interstellar",
                                "Interstellar",
                                "SF",
                                "Christopher Nolan",
                                LocalDate.of(2014, 11, 7),
                                22L),
                        new PeriodKeywordResponse(2,
                                "Christopher",
                                "Interstellar",
                                "SF",
                                "Christopher Nolan",
                                LocalDate.of(2014, 11, 7),
                                15L)
                )
        );
        given(searchService.topPeriod(2025, 1)).willReturn(serviceResponse);

        // when&then
        mockMvc.perform(get("/api/v1/movies/hot-keywords/period")
                        .param("year", "2025")
                        .param("month", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("월간 인기 검색어 조회 성공"))
                .andExpect(jsonPath("$.data.yearMonth").value("2025-01"))
                .andExpect(jsonPath("$.data.periodKeyword").isArray())
                .andExpect(jsonPath("$.data.periodKeyword[0].rank").value(1))
                .andExpect(jsonPath("$.data.periodKeyword[0].keyword").value("Interstellar"))
                .andExpect(jsonPath("$.data.periodKeyword[0].score").value(22));
    }

    @Test
    @DisplayName("월간 인기 검색어 - month 범위가 잘못되면 400")
    void testPeriod_invalidMonth_400() throws Exception {
        mockMvc.perform(get("/api/v1/movies/hot-keywords/period")
                        .param("year", "2025")
                        .param("month", "13"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("검색 월(month)은 1~12 사이여야 합니다."));
    }

}