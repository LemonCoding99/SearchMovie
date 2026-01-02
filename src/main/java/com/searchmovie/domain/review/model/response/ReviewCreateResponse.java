package com.searchmovie.domain.review.model.response;

import com.searchmovie.domain.review.model.dto.ReviewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewCreateResponse {

    private final Long id;
    private final Long movieId;
    private final Long userId;
    private final Integer rating;
    private final String content;
    private final LocalDateTime createdAt;

    public static ReviewCreateResponse from(ReviewDto dto) {
        return new ReviewCreateResponse(
                dto.getId(),
                dto.getMovieId(),
                dto.getUserId(),
                dto.getRating(),
                dto.getContent(),
                dto.getCreatedAt()
        );
    }
}
