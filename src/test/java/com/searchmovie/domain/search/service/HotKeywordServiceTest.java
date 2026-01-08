package com.searchmovie.domain.search.service;

import com.searchmovie.domain.search.model.request.PeriodRankRequest;
import com.searchmovie.domain.search.model.response.SynthesisRankResponse;
import com.searchmovie.domain.search.repository.HotKeywordRepository;
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
class HotKeywordServiceTest {

    @Mock
    private HotKeywordRepository hotKeywordRepository;

    @Mock
    private HotKeywordCacheService hotKeywordCacheService;

    @InjectMocks
    private HotKeywordService hotKeywordService;

    @Test
    @DisplayName("V1 종합 인기검색어 - 캐시 없이 DB에서 직접 조회한다")
    void v1SynthesisRanking_Success() {
        // given
        List<SynthesisRankResponse> mockResponse = List.of(new SynthesisRankResponse(1, "부장들", "남산의 부장들", "드라마", "우민호", LocalDate.parse("2020-07-15"), 100L));
        given(hotKeywordRepository.fetchSynthesisTop10()).willReturn(mockResponse);

        // when
        List<SynthesisRankResponse> result = hotKeywordService.v1SynthesisRanking();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKeyword()).isEqualTo("부장들");
        verify(hotKeywordRepository, times(1)).fetchSynthesisTop10();
    }

    @Test
    @DisplayName("V2 종합 인기검색어 - 서비스 로직 호출 시 DB 조회가 발생한다")
    void v2Synthesis_Ranking_Success() {
        // given
        List<SynthesisRankResponse> mockResponse = List.of(new SynthesisRankResponse(1, "부장들", "남산의 부장들", "드라마", "우민호", LocalDate.parse("2020-07-15"), 100L));
        given(hotKeywordRepository.fetchSynthesisTop10()).willReturn(mockResponse);

        // when
        List<SynthesisRankResponse> result = hotKeywordService.v2SynthesisRanking();

        // then
        assertThat(result).isEqualTo(mockResponse);
        verify(hotKeywordRepository, times(1)).fetchSynthesisTop10();
    }

    @Test
    @DisplayName("V3 종합 인기검색어 - 캐시가 있으면 DB를 조회하지 않고 캐시를 반환한다")
    void v3Synthesis_Ranking_CacheHit() {
        // given
        List<SynthesisRankResponse> cachedData = List.of(new SynthesisRankResponse(1, "캐시데이터", "영화제목", "장르", "감독", LocalDate.parse("2020-01-22"), 50L));
        given(hotKeywordCacheService.getSynthesis()).willReturn(cachedData);

        // when
        List<SynthesisRankResponse> result = hotKeywordService.v3SynthesisRanking();

        // then
        assertThat(result.get(0).getKeyword()).isEqualTo("캐시데이터");
        verify(hotKeywordRepository, never()).fetchSynthesisTop10(); // DB 조회 안함 확인
        verify(hotKeywordCacheService, times(1)).getSynthesis();
    }

    @Test
    @DisplayName("V3 종합 인기검색어 - 캐시가 없으면 DB 조회 후 캐시에 저장한다")
    void v3Synthesis_Ranking_CacheMiss() {
        // given
        given(hotKeywordCacheService.getSynthesis()).willReturn(null); // 캐시 없음
        List<SynthesisRankResponse> dbData = List.of(new SynthesisRankResponse(1, "DB데이터", "영화제목", "장르", "감독", LocalDate.parse("2024-01-01"), 50L));
        given(hotKeywordRepository.fetchSynthesisTop10()).willReturn(dbData);

        // when
        List<SynthesisRankResponse> result = hotKeywordService.v3SynthesisRanking();

        // then
        assertThat(result.get(0).getKeyword()).isEqualTo("DB데이터");
        verify(hotKeywordRepository, times(1)).fetchSynthesisTop10(); // DB 조회 발생
        verify(hotKeywordCacheService, times(1)).saveSynthesis(dbData); // 캐시에 저장 확인
    }

    @Test
    @DisplayName("V3 월간 검색어 - 캐시 없을 시 기간 계산 후 DB 조회 및 저장")
    void v3Period_Ranking_CacheMiss() {
        // given
        PeriodRankRequest request = new PeriodRankRequest(2023, 12);
        given(hotKeywordCacheService.getPeriod(2023, 12)).willReturn(null);
        given(hotKeywordRepository.fetchPeriodTop10(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of());

        // when
        hotKeywordService.v3PeriodRanking(request);

        // then
        verify(hotKeywordRepository, times(1)).fetchPeriodTop10(any(), any());
        verify(hotKeywordCacheService, times(1)).savePeriod(eq(2023), eq(12), any());
    }
}