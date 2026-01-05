package com.searchmovie.domain.review.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.common.model.PageResponse;
import com.searchmovie.domain.movie.entity.Movie;
import com.searchmovie.domain.movie.repository.MovieRepository;
import com.searchmovie.domain.review.entity.Review;
import com.searchmovie.domain.review.model.dto.ReviewDto;
import com.searchmovie.domain.review.model.request.ReviewCreateRequest;
import com.searchmovie.domain.review.model.request.ReviewUpdateRequest;
import com.searchmovie.domain.review.model.response.ReviewCreateResponse;
import com.searchmovie.domain.review.model.response.ReviewListResponse;
import com.searchmovie.domain.review.model.response.ReviewResponse;
import com.searchmovie.domain.review.repository.ReviewRepository;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewCreateResponse createReview(Long movieId, Long userId, ReviewCreateRequest request) {

        User foundUser = getUserOrThrow(userId);
        Movie foundMovie = getMovieOrThrow(movieId);

        validateReviewNotExists(userId, movieId);

        Review review = new Review(request.getRating(), request.getContent(), foundMovie, foundUser);
        Review savedReview = reviewRepository.save(review);

        return ReviewCreateResponse.from(ReviewDto.from(savedReview));
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long reviewId) {

        Review foundReview = getReviewOrThrow(reviewId);

        return ReviewResponse.from(ReviewDto.from(foundReview));
    }

    @Transactional(readOnly = true)
    public ReviewListResponse getReviews(Long movieId, String sort, Pageable pageable) {

        // 영화 존재 검증용
        getMovieOrThrow(movieId);

        Sort sortObj = parseReviewSort(sort);
        Pageable sortedPageable = applySort(pageable, sortObj);

        Page<Review> reviewPage = reviewRepository.findByMovie_Id(movieId, sortedPageable);

        Double avgRating = reviewRepository.findAvgRatingByMovie_Id(movieId);

        // 리뷰가 없는 경우 평균 평점은 0.0으로 내려줌
        if (avgRating == null) {
            avgRating = 0.0;
        }

        // 리뷰 페이지를 공통 PageResponse 형태로 변환
        PageResponse<ReviewResponse> page =
                PageResponse.from(
                        reviewPage.map(review -> ReviewResponse.from(ReviewDto.from(review)))
        );

        return new ReviewListResponse(avgRating, page);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, Long userId, ReviewUpdateRequest request) {

        // 유저 존재 검증용
        getUserOrThrow(userId);

        Review foundReview = getReviewOrThrow(reviewId);
        validateOwner(foundReview, userId);

        validateUpdateRequest(request);
        applyUpdate(foundReview, request);

        return ReviewResponse.from(ReviewDto.from(foundReview));
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {

        // 유저 존재 검증용
        getUserOrThrow(userId);

        Review foundReview = getReviewOrThrow(reviewId);
        validateOwner(foundReview, userId);

        foundReview.softDelete();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }

    private Movie getMovieOrThrow(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MOVIE_NOT_FOUND));
    }


    private Review getReviewOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REVIEW_NOT_FOUND));
    }

    private void validateReviewNotExists(Long userId, Long movieId) {
        // 한 유저는 한 영화에 리뷰를 하나만 작성 가능
        if (reviewRepository.existsByUser_IdAndMovie_Id(userId, movieId)) {
            throw new CustomException(ExceptionCode.REVIEW_ALREADY_EXISTS);
        }
    }

    private void validateOwner(Review review, Long userId) {
        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }
    }

    private Sort parseReviewSort(String sort) {
        // 허용 정렬: createdAt,desc / rating,desc
        String s = (sort == null || sort.isBlank()) ? "createdAt,desc" : sort;

        return switch (s) {
            case "createdAt,desc" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "rating,desc" -> Sort.by(Sort.Direction.DESC, "rating");
            default -> throw new CustomException(ExceptionCode.INVALID_REVIEW_SORT);
        };
    }

    private Pageable applySort(Pageable pageable, Sort sort) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    private void validateUpdateRequest(ReviewUpdateRequest request) {
        // 수정 요청 시 평점과 내용이 모두 null이면 수정 불가
        if (request.getRating() == null && request.getContent() == null) {
            throw new CustomException(ExceptionCode.NOTHING_TO_UPDATE);
        }
        if (request.getContent() != null && request.getContent().isBlank()) {
            throw new CustomException(ExceptionCode.INVALID_REVIEW_CONTENT);
        }
    }

    private void applyUpdate(Review review, ReviewUpdateRequest request) {
        if (request.getRating() != null) {
            review.updateRating(request.getRating());
        }
        if (request.getContent() != null) {
            review.updateContent(request.getContent());
        }
    }

}
