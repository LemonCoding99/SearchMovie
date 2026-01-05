package com.searchmovie.common.utils;

import com.searchmovie.domain.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    //JWT 토큰 접두사
    private static final String BEARER_PREFIX = "Bearer ";
    // jwt 토큰 만료 시간(60분)
    private static final long TOKEN_TIME = 60 * 60 * 1000L;
    //jwt 서명 알고리즘
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // .properties에서 설정한 비밀키
    @Value("${jwt.secret.key}")
    private String secretKey;
    // 실제 서명에 사용되는 키
    private SecretKey key;

    // 키 초기화
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public String generateToken(User user) {

        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .subject(user.getUsername())
                        .claim("auth", user.getRole())
                        .claim("userId", user.getId())
                        .expiration(new Date(date.getTime() + TOKEN_TIME))
                        .issuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String jwt) {

        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);
            return true;    // 토큰이 유효할 경우 true
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            // 토큰 서명이 잘못되었거나 잘못된 형식의 jwt인 경우
            log.error("유효하지 않는 JWT 서명 입니다.", e);
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            log.error("만료된 JWT token 입니다.", e);
        } catch (UnsupportedJwtException e) {
            // 전달받은 토큰이 지원되지 않는 형식인 경우
            log.error("지원되지 않는 JWT 토큰 입니다.", e);
        } catch (IllegalArgumentException e) {
            // jwt 내용이 비어있거나 잘못된 형식인 경우
            log.error("잘못된 JWT 토큰 입니다.", e);
        }

        return false;   // 토큰이 유효하지 않을 시 false
    }

    // 토큰에서 내용 전부 뽑아내기
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .setSigningKey(key) // 비밀 키를 사용하여 서명 검증
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰에서 권한 정보 뽑아내기
    public String extractRole(String token) {
        return extractAllClaims(token).get("auth", String.class);
    }

    public long extractUserId(String token) {

        if (extractAllClaims(token).get("userId", Long.class) == null) {
            return 0;
        }
        return extractAllClaims(token).get("userId", Long.class);
    }
}