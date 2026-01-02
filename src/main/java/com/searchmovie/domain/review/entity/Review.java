package com.searchmovie.domain.review.entity;

import com.searchmovie.common.entity.BaseEntity;
import com.searchmovie.domain.movie.entity.Movie;
import com.searchmovie.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Review(Integer rating, String content, Movie movie, User user) {
        this.rating = rating;
        this.content = content;
        this.movie = movie;
        this.user = user;
    }

    public void update(Integer rating, String content) {
        this.rating = rating;
        this.content = content;
    }
}
