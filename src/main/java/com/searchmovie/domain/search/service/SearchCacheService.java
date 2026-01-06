package com.searchmovie.domain.search.service;

import com.searchmovie.domain.search.model.response.GenreKeywordResponse;
import com.searchmovie.domain.search.model.response.HotKeywordResponse;
import com.searchmovie.domain.search.model.response.PeriodSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Duration TTL = Duration.ofMinutes(5);

    private static final String KEY_SYNTHESIS = "hot:v3:synthesis";
    private static final String KEY_GENRE = "hot:v3:genre";

    private static String KEY_PERIOD(int year, int month) {
        return "hot:v3:period:" + year + "-" + String.format("%02d", month);
    }

    // =========================
    // Synthesis
    // =========================
    public void saveSynthesis(List<HotKeywordResponse> items) {
        redisTemplate.opsForValue().set(KEY_SYNTHESIS, new HotKeywordListWrapper(items), TTL);
        log.info("[REDIS_SAVE] key={}", KEY_SYNTHESIS);
    }

    public List<HotKeywordResponse> getSynthesis() {
        Object cached = redisTemplate.opsForValue().get(KEY_SYNTHESIS);
        if (cached instanceof HotKeywordListWrapper wrapper) {
            log.info("[REDIS_HIT] key={}", KEY_SYNTHESIS);
            return wrapper.items();
        }
        log.info("[REDIS_MISS] key={}", KEY_SYNTHESIS);
        return null; // 없으면 null로 넘기고, 호출한 서비스가 DB 조회하면 됨
    }

    // =========================
    // Genre
    // =========================
    public void saveGenre(List<GenreKeywordResponse> items) {
        redisTemplate.opsForValue().set(KEY_GENRE, new GenreKeywordListWrapper(items), TTL);
        log.info("[REDIS_SAVE] key={}", KEY_GENRE);
    }

    public List<GenreKeywordResponse> getGenre() {
        Object cached = redisTemplate.opsForValue().get(KEY_GENRE);
        if (cached instanceof GenreKeywordListWrapper wrapper) {
            log.info("[REDIS_HIT] key={}", KEY_GENRE);
            return wrapper.items();
        }
        log.info("[REDIS_MISS] key={}", KEY_GENRE);
        return null;
    }

    // =========================
    // Period
    // =========================
    public void savePeriod(int year, int month, PeriodSearchResponse response) {
        String key = KEY_PERIOD(year, month);
        redisTemplate.opsForValue().set(key, response, TTL);
        log.info("[REDIS_SAVE] key={}", key);
    }

    public PeriodSearchResponse getPeriod(int year, int month) {
        String key = KEY_PERIOD(year, month);
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof PeriodSearchResponse period) {
            log.info("[REDIS_HIT] key={}", key);
            return period;
        }
        log.info("[REDIS_MISS] key={}", key);
        return null;
    }

    // ✅ List 타입 깨짐 방지용 Wrapper
    public record HotKeywordListWrapper(List<HotKeywordResponse> items) {}
    public record GenreKeywordListWrapper(List<GenreKeywordResponse> items) {}
}
