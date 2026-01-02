package com.searchmovie.domain.review.model.dto;

import com.searchmovie.domain.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewDto {

    private final Long id;
    private final Long movieId;
    private final Long userId;
    private final Integer rating;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ReviewDto(Long id, Long movieId, Long userId, Integer rating, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.movieId = movieId;
        this.userId = userId;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReviewDto from(Review review) {
        return new ReviewDto(
                review.getId(),
                review.getMovie().getId(),
                review.getUser().getId(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
