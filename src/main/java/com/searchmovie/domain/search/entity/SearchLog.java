package com.searchmovie.domain.search.entity;

import com.searchmovie.domain.movie.entity.Movie;
import com.searchmovie.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "search_logs")
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(nullable = false, length = 255)
    private String keyword;

    @Column(name = "searched_at", nullable = false)
    private LocalDateTime searchedAt;

    public SearchLog(User user, Movie movie, String keyword) {
        this.userId = user.getId();
        this.movieId = movie.getId();
        this.keyword = keyword;
        this.searchedAt = LocalDateTime.now();
    }
}
