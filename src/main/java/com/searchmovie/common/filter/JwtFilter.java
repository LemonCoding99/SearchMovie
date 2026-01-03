package com.searchmovie.common.filter;

import com.searchmovie.common.utils.JwtUtil;
import com.searchmovie.domain.user.entity.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = null;

        String authorizationHeader = request.getHeader("Authorization");

        // jwt토큰 검사
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 있을 시 Bearer 빼낸 순수 jwt토큰 추출
        jwt = authorizationHeader.substring(7);

        // jwt토큰 유효성 검사
        if (!jwtUtil.validateToken(jwt)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{{\"error\": \"Unauthorized\"}}");
            return;
        }

        // jwt토큰이 유효할 시 토큰 정보 추출
        String role = jwtUtil.extractRole(jwt);
        long userId = jwtUtil.extractUserId(jwt);

        UserRole userRole = UserRole.valueOf(role);
        Collection<? extends GrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority("ROLE_" + userRole.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                authorityList
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
        System.out.println("end");
    }
}
