package com.searchmovie.domain.movie.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table( // 영화 하나가 같은 장르를 여러개 가질 경우를 방지
        name = "movie_genres",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_movie_genre",
                        columnNames = {"movie_id", "genre_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "genre_id", nullable = false)
    private Long genreId;

    private MovieGenre(Long movieId, Long genreId) {
        this.movieId = movieId;
        this.genreId = genreId;
    }

    public static MovieGenre of(Long movieId, Long genreId) {
        if (movieId == null || genreId == null) {
            throw new IllegalArgumentException("movieId와 genreId는 필수입니다.");
        }
        return new MovieGenre(movieId, genreId);
    }
}
