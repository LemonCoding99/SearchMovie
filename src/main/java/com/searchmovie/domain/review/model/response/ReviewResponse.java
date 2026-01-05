package com.searchmovie.domain.review.model.response;

import com.searchmovie.domain.review.model.dto.ReviewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponse {

    private final Long id;
    private final Long movieId;
    private final Long userId;
    private final Integer rating;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ReviewResponse from(ReviewDto dto) {
        return new ReviewResponse(
                dto.getId(),
                dto.getMovieId(),
                dto.getUserId(),
                dto.getRating(),
                dto.getContent(),
                dto.getCreatedAt(),
                dto.getUpdatedAt()
        );
    }
}
