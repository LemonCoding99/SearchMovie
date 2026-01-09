//package com.searchmovie.domain.search.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.searchmovie.domain.search.model.response.GenreKeywordResponse;
//import com.searchmovie.domain.search.model.response.HotKeywordResponse;
//import com.searchmovie.domain.search.model.response.PeriodSearchResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class SearchCacheService {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final ObjectMapper objectMapper;
//
//    private static final Duration TTL = Duration.ofMinutes(5);
//
//    private static final String KEY_SYNTHESIS = "hot:v3:synthesis";
//    private static final String KEY_GENRE = "hot:v3:genre";
//
//    private static String KEY_PERIOD(int year, int month) {
//        return "hot:v3:period:" + year + "-" + String.format("%02d", month);
//    }
//
//    // =========================
//    // Synthesis
//    // =========================
//    public void saveSynthesis(List<HotKeywordResponse> items) {
//
//        try {
//            HotKeywordListWrapper wrapper = new HotKeywordListWrapper(items);
//            String json = objectMapper.writeValueAsString(wrapper);
//            redisTemplate.opsForValue().set(KEY_SYNTHESIS, json, TTL);
//            log.info("[REDIS_SAVE] key={}", KEY_SYNTHESIS);
//        } catch (JsonProcessingException e) {
//            log.error("[REDIS_SAVE_ERROR] 저장 실패: {}", e.getMessage());
//        }
//    }
//
//    public List<HotKeywordResponse> getSynthesis() {
//        try {
//            Object cached = redisTemplate.opsForValue().get(KEY_SYNTHESIS);
//            if (cached == null) {
//                log.info("[REDIS_MISS] key={}", KEY_SYNTHESIS);
//                return null;
//            }
//            HotKeywordListWrapper wrapper = objectMapper.readValue(cached.toString(), HotKeywordListWrapper.class);
//            log.info("[REDIS_HIT] key={}", KEY_SYNTHESIS);
//            return wrapper.items();
//        } catch (Exception e) {
//            log.error("[REDIS_ERROR] Synthesis 역직렬화 실패: {}", e.getMessage());
//            return null;
//        }
//    }
//
//
//    // =========================
//    // Genre
//    // =========================
//    public void saveGenre(List<GenreKeywordResponse> items) {
//        try {
//            String json = objectMapper.writeValueAsString(new GenreKeywordListWrapper(items));
//            redisTemplate.opsForValue().set(KEY_GENRE, json, TTL);
//            log.info("[REDIS_SAVE] key={}", KEY_GENRE);
//        } catch (JsonProcessingException e) {
//            log.error("[REDIS_SAVE_ERROR] Genre 저장 실패: {}", e.getMessage());
//        }
//    }
//
//    public List<GenreKeywordResponse> getGenre() {
//        try {
//            Object cached = redisTemplate.opsForValue().get(KEY_GENRE);
//            if (cached == null) {
//                log.info("[REDIS_MISS] key={}", KEY_GENRE);
//                return null;
//            }
//            GenreKeywordListWrapper wrapper = objectMapper.readValue(cached.toString(), GenreKeywordListWrapper.class);
//            log.info("[REDIS_HIT] key={}", KEY_GENRE);
//            return wrapper.items();
//        } catch (Exception e) {
//            log.error("[REDIS_ERROR] Genre 역직렬화 실패: {}", e.getMessage());
//            return null;
//        }
//    }
//
//
//    // =========================
//    // Period
//    // =========================
//    public void savePeriod(int year, int month, PeriodSearchResponse response) {
//        String key = KEY_PERIOD(year, month);
//        try {
//            String json = objectMapper.writeValueAsString(new PeriodSearchWrapper(response));
//            redisTemplate.opsForValue().set(key, json, TTL);
//            log.info("[REDIS_SAVE] key={}", key);
//        } catch (Exception e) {
//            log.error("[REDIS_SAVE_ERROR] Period 저장 실패: {}", e.getMessage());
//        }
//    }
//
//    public PeriodSearchResponse getPeriod(int year, int month) {
//        String key = KEY_PERIOD(year, month);
//        try {
//            Object cached = redisTemplate.opsForValue().get(key);
//            if (cached == null) {
//                log.info("[REDIS_MISS] key={}", key);
//            return null;
//            }
//
//            PeriodSearchWrapper wrapper = objectMapper.readValue(cached.toString(), PeriodSearchWrapper.class);
//            log.info("[REDIS_HIT] key={}", key);
//            return wrapper.response();
//        } catch (Exception e) {
//            log.error("[REDIS_ERROR] Period 역직렬화 실패: {}", e.getMessage(), e);
//            return null;
//        }
//    }
//
//    public record HotKeywordListWrapper(List<HotKeywordResponse> items) {
//    }
//    public record GenreKeywordListWrapper(List<GenreKeywordResponse> items) {
//    }
//    public record PeriodSearchWrapper(PeriodSearchResponse response) { }
//}
