package com.searchmovie.domain.search.entity;

import com.searchmovie.domain.movie.entity.Genre;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(nullable = false, length = 255)
    private String keyword;

    @Column(name = "genre_id", nullable = false)
    private Long genreId;

    @Column(nullable = false)
    private Long count;

    @Column(name = "searched_at", nullable = false)
    private LocalDateTime searchedAt;

    public SearchLog(User user, Long movieId, String keyword, Long genreId) {
        this.user = user;
        this.movieId = movieId;
        this.keyword = keyword;
        this.genreId = genreId;
        this.count = 1L;
        this.searchedAt = LocalDateTime.now();
    }

}
