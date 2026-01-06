package com.searchmovie.domain.search.service;

import com.searchmovie.domain.search.model.request.PeriodSearchRequest;
import com.searchmovie.domain.search.model.response.HotKeywordResponse;
import com.searchmovie.domain.search.repository.SearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private SearchCacheService searchCacheService;

    @InjectMocks
    private SearchService searchService;

    @Test
    @DisplayName("V1 종합 인기검색어 - 캐시 없이 DB에서 직접 조회한다")
    void v1topOverall_Success() {
        // given
        List<HotKeywordResponse> mockResponse = List.of(new HotKeywordResponse(1, "부장들", "남산의 부장들", "드라마", "우민호", LocalDate.parse("2020-07-15"), 100L));
        given(searchRepository.findTopKeywords()).willReturn(mockResponse);

        // when
        List<HotKeywordResponse> result = searchService.v1topOverall();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKeyword()).isEqualTo("부장들");
        verify(searchRepository, times(1)).findTopKeywords();
    }

    @Test
    @DisplayName("V2 종합 인기검색어 - 서비스 로직 호출 시 DB 조회가 발생한다 (AOP 캐시는 통합테스트에서 검증)")
    void v2topSynthesis_Success() {
        // given
        List<HotKeywordResponse> mockResponse = List.of(new HotKeywordResponse(1, "부장들", "남산의 부장들", "드라마", "우민호", LocalDate.parse("2020-07-15"), 100L));
        given(searchRepository.findTopKeywords()).willReturn(mockResponse);

        // when
        List<HotKeywordResponse> result = searchService.v2topSynthesis();

        // then
        assertThat(result).isEqualTo(mockResponse);
        verify(searchRepository, times(1)).findTopKeywords();
    }

    @Test
    @DisplayName("V3 종합 인기검색어 - 캐시가 있으면 DB를 조회하지 않고 캐시를 반환한다")
    void v3topSynthesis_CacheHit() {
        // given
        List<HotKeywordResponse> cachedData = List.of(new HotKeywordResponse(1, "캐시데이터", "영화제목", "장르", "감독", LocalDate.parse("2020-01-22"), 50L));
        given(searchCacheService.getSynthesis()).willReturn(cachedData);

        // when
        List<HotKeywordResponse> result = searchService.v3topSynthesis();

        // then
        assertThat(result.get(0).getKeyword()).isEqualTo("캐시데이터");
        verify(searchRepository, never()).findTopKeywords(); // DB 조회 안함 확인
        verify(searchCacheService, times(1)).getSynthesis();
    }

    @Test
    @DisplayName("V3 종합 인기검색어 - 캐시가 없으면 DB 조회 후 캐시에 저장한다")
    void v3topSynthesis_CacheMiss() {
        // given
        given(searchCacheService.getSynthesis()).willReturn(null); // 캐시 없음
        List<HotKeywordResponse> dbData = List.of(new HotKeywordResponse(1, "DB데이터", "영화제목", "장르", "감독", LocalDate.parse("2024-01-01"), 50L));
        given(searchRepository.findTopKeywords()).willReturn(dbData);

        // when
        List<HotKeywordResponse> result = searchService.v3topSynthesis();

        // then
        assertThat(result.get(0).getKeyword()).isEqualTo("DB데이터");
        verify(searchRepository, times(1)).findTopKeywords(); // DB 조회 발생
        verify(searchCacheService, times(1)).saveSynthesis(dbData); // 캐시에 저장 확인
    }

    @Test
    @DisplayName("V3 월간 검색어 - 캐시 없을 시 기간 계산 후 DB 조회 및 저장")
    void v3topPeriod_CacheMiss() {
        // given
        PeriodSearchRequest request = new PeriodSearchRequest(2023, 12);
        given(searchCacheService.getPeriod(2023, 12)).willReturn(null);

        // Repository 결과 모킹 (내용은 중요치 않음)
        given(searchRepository.findTopPeriod(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of());

        // when
        searchService.v3topPeriod(request);

        // then
        verify(searchRepository, times(1)).findTopPeriod(any(), any());
        verify(searchCacheService, times(1)).savePeriod(eq(2023), eq(12), any());
    }
}