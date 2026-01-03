package com.searchmovie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Profile("!test")
@Configuration
@EnableJpaAuditing
@SpringBootApplication
public class SearchMovieApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchMovieApplication.class, args);
    }
}
