package com.searchmovie.domain.review.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.movie.entity.Movie;
import com.searchmovie.domain.movie.repository.MovieRepository;
import com.searchmovie.domain.review.entity.Review;
import com.searchmovie.domain.review.model.dto.ReviewDto;
import com.searchmovie.domain.review.model.request.ReviewCreateRequest;
import com.searchmovie.domain.review.model.response.ReviewCreateResponse;
import com.searchmovie.domain.review.model.response.ReviewGetResponse;
import com.searchmovie.domain.review.repository.ReviewRepository;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
}
