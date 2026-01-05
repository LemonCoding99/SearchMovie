package com.searchmovie.domain.review.model.response;

import com.searchmovie.common.model.PageResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewListResponse {

    private Double avgRating;
    private PageResponse<ReviewResponse> page;
}
