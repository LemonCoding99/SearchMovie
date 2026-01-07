package com.searchmovie.domain.movie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchmovie.domain.movie.model.response.MovieSearchResponse;
import com.searchmovie.domain.movie.model.response.SimplePageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieSearchCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;  // 자바 객체 ↔ JSON 문자열 변환

    private static final String CACHE_SEARCH_PREFIX = "search:";
    private static final long TTL_MINUTES = 5;  // TTL 시간

    // 캐시 조회
    public SimplePageResponse<MovieSearchResponse> getSearchCache(String title, String director, String genreKeyword, LocalDate releaseDateStart, LocalDate releaseDateEnd, int page, int size) {
    String key = buildSearchKey(title, director, genreKeyword, releaseDateStart, releaseDateEnd, page, size);

    // Redis에 해당 key값이 있으면 가져오고 없으면 CacheMiss 처리
    try {
        log.info("key={}", key);
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached == null) return null;  // 캐시가 없는 경우 null return

        // 타입 변환
        // 이미 원하는 타입이면 그대로 반환하기
        if (cached instanceof SimplePageResponse) {
            SimplePageResponse<MovieSearchResponse> newType = (SimplePageResponse<MovieSearchResponse>) cached;
            return newType;
        }

        // 맵 형태라면 DTO로 변환하기
        return objectMapper.convertValue(cached,
                new com.fasterxml.jackson.core.type.TypeReference<SimplePageResponse<MovieSearchResponse>>() {}  // 익명 클래스 생성
        );
    } catch (Exception e) {
        log.warn("Redis cache get failed. key={}", key, e);  // 오류 생길경우 로그 남기기
        return null;
    }
    }

    // 캐시 저장하기
    public void saveSearchCache(String title, String director, String genreKeyword, LocalDate releaseDateStart, LocalDate releaseDateEnd, int page, int size, SimplePageResponse<MovieSearchResponse> value) {
        String key = buildSearchKey(title, director, genreKeyword, releaseDateStart, releaseDateEnd, page, size);

        try {
            log.info("key={}", key);
            redisTemplate.opsForValue().set(key, value, TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis cache save failed. key={}", key, e);
        }
    }

    // 검색 캐시 키 생성 메서드 (String으로 만들어주기)
    private String buildSearchKey(String title, String director, String genreKeyword, LocalDate start, LocalDate end, int page, int size) {
        return CACHE_SEARCH_PREFIX
                + "title=" + normalize(title)  //
                + ":director=" + normalize(director)
                + ":genre=" + normalize(genreKeyword)
                + ":start=" + (start == null ? "null" : start.toString()) // 시작일이 없으면 "null", 있으면 날짜 문자열
                + ":end=" + (end == null ? "null" : end.toString()) // 종료일이 없으면 "null", 있으면 날짜 문자열
                + ":page=" + page
                + ":size=" + size;
    }

    // 키 값 정규화 메서드
    private String normalize(String keyword) {
        if (keyword == null) return "null";  // 입력이 null일 경우 null
        String normalizedKeyword  = keyword.trim().toLowerCase();  // 앞뒤 공백 제거 + 소문자 통일
        normalizedKeyword = normalizedKeyword .replaceAll("\\s+", " ");  // 연속된 공백을 1개 공백으로 치환
        return normalizedKeyword .isEmpty() ? "null" : normalizedKeyword ;  // 빈 문자열이면 null과 동일 취급
    }
}
