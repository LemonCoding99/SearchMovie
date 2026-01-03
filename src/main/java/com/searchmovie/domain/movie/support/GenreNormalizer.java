package com.searchmovie.domain.movie.support;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GenreNormalizer {

    private static final Map<String, String> ALIAS_TO_STD = Map.ofEntries(
            // 액션
            Map.entry(key("action"), "액션"),
            Map.entry(key("액션"), "액션"),

            // 어드벤처
            Map.entry(key("adventure"), "어드벤처"),
            Map.entry(key("어드벤처"), "어드벤처"),

            // 애니메이션
            Map.entry(key("animation"), "애니메이션"),
            Map.entry(key("애니"), "애니메이션"),
            Map.entry(key("애니메이션"), "애니메이션"),

            // 전기
            Map.entry(key("biography"), "전기"),
            Map.entry(key("전기"), "전기"),

            // 코미디
            Map.entry(key("comedy"), "코미디"),
            Map.entry(key("코미디"), "코미디"),

            // 범죄
            Map.entry(key("crime"), "범죄"),
            Map.entry(key("범죄"), "범죄"),

            // 다큐멘터리
            Map.entry(key("documentary"), "다큐멘터리"),
            Map.entry(key("다큐"), "다큐멘터리"),
            Map.entry(key("다큐멘터리"), "다큐멘터리"),

            // 드라마
            Map.entry(key("drama"), "드라마"),
            Map.entry(key("드라마"), "드라마"),

            // 가족
            Map.entry(key("family"), "가족"),
            Map.entry(key("가족"), "가족"),

            // 판타지
            Map.entry(key("fantasy"), "판타지"),
            Map.entry(key("판타지"), "판타지"),

            // 역사
            Map.entry(key("history"), "역사"),
            Map.entry(key("사극"), "역사"),
            Map.entry(key("역사"), "역사"),

            // 공포
            Map.entry(key("horror"), "공포"),
            Map.entry(key("호러"), "공포"),
            Map.entry(key("공포"), "공포"),

            // 음악
            Map.entry(key("music"), "음악"),
            Map.entry(key("음악"), "음악"),

            // 뮤지컬
            Map.entry(key("musical"), "뮤지컬"),
            Map.entry(key("뮤지컬"), "뮤지컬"),

            // 미스터리
            Map.entry(key("mystery"), "미스터리"),
            Map.entry(key("미스터리"), "미스터리"),

            // 로맨스
            Map.entry(key("romance"), "로맨스"),
            Map.entry(key("멜로"), "로맨스"),
            Map.entry(key("멜로/로맨스"), "로맨스"),
            Map.entry(key("로맨스"), "로맨스"),

            // SF (key()가 대소문자/표기 통일함)
            Map.entry(key("sci-fi"), "SF"),
            Map.entry(key("science fiction"), "SF"),
            Map.entry(key("sf"), "SF"),


            // 스포츠
            Map.entry(key("sport"), "스포츠"),
            Map.entry(key("sports"), "스포츠"),
            Map.entry(key("스포츠"), "스포츠"),

            // 스릴러
            Map.entry(key("thriller"), "스릴러"),
            Map.entry(key("스릴러"), "스릴러"),

            // 전쟁
            Map.entry(key("war"), "전쟁"),
            Map.entry(key("전쟁"), "전쟁"),

            // 서부
            Map.entry(key("western"), "서부"),
            Map.entry(key("서부극"), "서부"),
            Map.entry(key("서부"), "서부"),

            // 느와르 (key()가 통일해줌)
            Map.entry(key("film-noir"), "느와르"),
            Map.entry(key("noir"), "느와르"),
            Map.entry(key("느와르"), "느와르"),

            // 기타
            Map.entry(key("reality-tv"), "기타"),
            Map.entry(key("talk-show"), "기타"),
            Map.entry(key("game-show"), "기타"),
            Map.entry(key("news"), "기타"),
            Map.entry(key("기타"), "기타")
    );

    public List<String> normalize(List<String> rawGenres) {
        if (rawGenres == null || rawGenres.isEmpty()) return List.of();

        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String token : rawGenres) {
            if (token == null) continue;

            String t = token.trim();
            if (t.isEmpty()) continue;

            String k = key(t);
            out.add(ALIAS_TO_STD.getOrDefault(k, t));
        }
        return new ArrayList<>(out);
    }

    // (선택) raw 문자열용 필요하면 유지
    public static Set<String> normalizeToKoSet(String raw) {
        if (raw == null) return Set.of();
        String s = raw.trim();
        if (s.isEmpty() || "\\N".equals(s)) return Set.of();

        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1);
        }

        String[] parts = s.split("[,\\/|]+");
        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String p : parts) {
            if (p == null) continue;
            String token = p.trim();
            if (token.isEmpty()) continue;

            token = token.replaceAll("\\s*\\(.*?\\)", "").trim();
            if (token.isEmpty()) continue;

            String k = key(token);
            out.add(ALIAS_TO_STD.getOrDefault(k, token));
        }
        return out;
    }

    private static String key(String token) {
        if (token == null) return "";

        String t = token.trim().toLowerCase(Locale.ROOT);
        t = t.replaceAll("\\s+", " ");
        t = t.replace('–', '-').replace('—', '-');
        t = t.replace('_', '-');

        if (t.equals("sci fi") || t.equals("scifi") || t.equals("sci-fi")) return "sci-fi";
        if (t.equals("film noir") || t.equals("film-noir")) return "film-noir";

        return t;
    }
}
