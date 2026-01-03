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
import com.searchmovie.domain.review.model.response.ReviewGetResponse;
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

    // JWT 적용 전까지 임시로 userId 적용
    @Transactional
    public ReviewCreateResponse createReview(Long movieId, Long userId, ReviewCreateRequest request) {

        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Movie foundMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MOVIE_NOT_FOUND));

        if (reviewRepository.existsByUser_IdAndMovie_Id(userId, movieId)) {
            throw new CustomException(ExceptionCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = new Review(request.getRating(), request.getContent(), foundMovie, foundUser);
        Review savedReview = reviewRepository.save(review);

        return ReviewCreateResponse.from(ReviewDto.from(savedReview));
    }

    @Transactional(readOnly = true)
    public ReviewGetResponse getReview(Long reviewId) {

        Review foundReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REVIEW_NOT_FOUND));

        return ReviewGetResponse.from(ReviewDto.from(foundReview));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewGetResponse> getReviews(Long movieId, String sort, Pageable pageable) {

        // 존재 검증용
        movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MOVIE_NOT_FOUND));

        String s = (sort == null || sort.isBlank()) ? "createdAt,desc" : sort;

        Sort sortObj;
        if (s.equals("createdAt,desc")) {
            sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        } else if (s.equals("rating,desc")) {
            sortObj = Sort.by(Sort.Direction.DESC, "rating");
        } else {
            throw new CustomException(ExceptionCode.INVALID_REVIEW_SORT);
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);

        Page<Review> reviewPage = reviewRepository.findByMovie_Id(movieId, sortedPageable);

        Page<ReviewGetResponse> dtoPage = reviewPage.map(review -> ReviewGetResponse.from(ReviewDto.from(review)));

        return PageResponse.from(dtoPage);
    }

    // JWT 적용 전까지 임시로 userId 적용
    @Transactional
    public ReviewGetResponse updateReview(Long reviewId, Long userId, ReviewUpdateRequest request) {

        // 유저 존재 검증용
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Review foundReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REVIEW_NOT_FOUND));

        if (!foundReview.getUser().getId().equals(userId)) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }

        if (request.getRating() == null && request.getContent() == null) {
            throw new CustomException(ExceptionCode.NOTHING_TO_UPDATE);
        }

        if (request.getRating() != null) {
            foundReview.updateRating(request.getRating());
        }

        if (request.getContent() != null) {

            if (request.getContent().isBlank()) {
                throw new CustomException(ExceptionCode.INVALID_REVIEW_CONTENT);
            }

            foundReview.updateContent(request.getContent());
        }

        return ReviewGetResponse.from(ReviewDto.from(foundReview));
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {

        // 유저 존재 검증용
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Review foundReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REVIEW_NOT_FOUND));

        if (!foundReview.getUser().getId().equals(userId)) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }

        foundReview.softDelete();
    }
}
