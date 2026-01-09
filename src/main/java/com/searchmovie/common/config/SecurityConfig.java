package com.searchmovie.common.config;

import com.searchmovie.common.filter.JwtFilter;
import com.searchmovie.domain.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, SecurityContextHolderAwareRequestFilter.class)
                .authorizeHttpRequests(auth -> auth
                                // 권한 없어도 허용
                                .requestMatchers("/api/auth/login").permitAll()
                                .requestMatchers("/api/auth/signup").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                                .requestMatchers("/api/v1/movies/**").permitAll()
                                .requestMatchers("/api/v2/movies/**").permitAll()
                                .requestMatchers("/actuator/health").permitAll()
                                .requestMatchers("/api/stocks/**").permitAll()
                                // User 권한만 허용

                                // ADMIN 권한만 허용
                                .requestMatchers(HttpMethod.PUT, "/api/movies/**").hasAuthority(UserRole.Authority.ADMIN)
                                .requestMatchers(HttpMethod.DELETE, "/api/movies/**").hasAuthority(UserRole.Authority.ADMIN)
                                .requestMatchers("/api/coupons/*/stocks").hasAuthority(UserRole.Authority.ADMIN)
                                .requestMatchers("/api/stocks/**").hasAuthority(UserRole.Authority.ADMIN)
                                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()

                                .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/api/v1/movies/**")
                .requestMatchers("/api/movies/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}