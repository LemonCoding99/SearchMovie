package com.searchmovie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableCaching // 캐시 기능 활성화
public class SearchMovieApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchMovieApplication.class, args);
    }

}
