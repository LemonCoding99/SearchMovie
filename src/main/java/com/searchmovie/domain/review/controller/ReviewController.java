package com.searchmovie.domain.review.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.common.model.PageResponse;
import com.searchmovie.domain.review.model.request.ReviewCreateRequest;
import com.searchmovie.domain.review.model.request.ReviewUpdateRequest;
import com.searchmovie.domain.review.model.response.ReviewCreateResponse;
import com.searchmovie.domain.review.model.response.ReviewGetResponse;
import com.searchmovie.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    // JWT 적용 전까지 임시로 userId 적용
    @PostMapping("/movies/{movieId}/users/{userId}/reviews")
    public ResponseEntity<CommonResponse<ReviewCreateResponse>> createReview(
            @PathVariable Long movieId,
            @PathVariable Long userId,
            @Valid @RequestBody ReviewCreateRequest request) {

        ReviewCreateResponse result = reviewService.createReview(movieId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(result, "리뷰 생성 성공"));
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonResponse<ReviewGetResponse>> getReview(@PathVariable Long reviewId) {

        ReviewGetResponse result = reviewService.getReview(reviewId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success(result, "리뷰 조회 성공"));
    }

    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<CommonResponse<PageResponse<ReviewGetResponse>>> getReviews(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        PageResponse<ReviewGetResponse> result = reviewService.getReviews(movieId, sort, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success(result, "리뷰 목록 조회 성공"));
    }

    // JWT 적용 전까지 임시로 userId 적용
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonResponse<ReviewGetResponse>> updateReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId,
            @Valid @RequestBody ReviewUpdateRequest request) {

        ReviewGetResponse result = reviewService.updateReview(reviewId, userId, request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success(result, "리뷰 수정 성공"));
    }
}
