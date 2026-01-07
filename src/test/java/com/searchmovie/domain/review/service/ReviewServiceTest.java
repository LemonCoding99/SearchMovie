package com.searchmovie.domain.review.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.movie.entity.Movie;
import com.searchmovie.domain.movie.repository.MovieRepository;
import com.searchmovie.domain.review.entity.Review;
import com.searchmovie.domain.review.model.request.ReviewCreateRequest;
import com.searchmovie.domain.review.model.request.ReviewUpdateRequest;
import com.searchmovie.domain.review.model.response.ReviewCreateResponse;
import com.searchmovie.domain.review.model.response.ReviewResponse;
import com.searchmovie.domain.review.repository.ReviewRepository;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_success() {

        // given
        Long userId = 1L;
        Long movieId = 1L;

        User user = new User("유저", "user1", "pw", "u1@gmail.com");
        Movie movie = new Movie("영화", "감독", LocalDate.now());
        ReviewCreateRequest request = new ReviewCreateRequest(5, "재밌게 봤습니다.");
        Review review = new Review(request.getRating(), request.getContent(), movie, user);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));
        given(reviewRepository.existsByUser_IdAndMovie_Id(1L, 1L)).willReturn(false);
        given(reviewRepository.save(any(Review.class))).willReturn(review);

        // when
        ReviewCreateResponse response = reviewService.createReview(movieId, userId, request);

        // then
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getContent()).isEqualTo("재밌게 봤습니다.");
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 유저가 존재하지 않을 때")
    void createReview_fail_1번_케이스() {

        // given
        Long userId = 1L;
        Long movieId = 1L;
        ReviewCreateRequest request = new ReviewCreateRequest(5, "재밌게 봤습니다.");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.createReview(movieId, userId, request));

        // then
        assertEquals(ExceptionCode.USER_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 영화가 존재하지 않을 때")
    void createReview_fail_2번_케이스() {

        // given
        Long userId = 1L;
        Long movieId = 1L;

        User user = new User("유저", "user1", "pw", "u1@gmail.com");
        ReviewCreateRequest request = new ReviewCreateRequest(5, "재밌게 봤습니다.");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(movieRepository.findById(movieId)).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.createReview(movieId, userId, request));

        // then
        assertEquals(ExceptionCode.MOVIE_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 유저가 리뷰를 중복으로 작성했을 때")
    void createReview_fail_3번_케이스() {

        // given
        Long userId = 1L;
        Long movieId = 1L;

        User user = new User("유저", "user1", "pw", "u1@gmail.com");
        Movie movie = new Movie("영화", "감독", LocalDate.now());
        ReviewCreateRequest request = new ReviewCreateRequest(5, "재밌게 봤습니다.");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));
        given(reviewRepository.existsByUser_IdAndMovie_Id(1L, 1L)).willReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.createReview(movieId, userId, request));

        // then
        assertEquals(ExceptionCode.REVIEW_ALREADY_EXISTS, exception.getExceptionCode());
    }

    @Test
    @DisplayName("리뷰 조회 성공")
    void getReview_success() {

        // given
        Long reviewId = 1L;

        User user = new User("유저", "user1", "pw", "u1@gmail.com");
        Movie movie = new Movie("영화", "감독", LocalDate.now());
        Review review = new Review(5, "재밌게 봤습니다.", movie, user);

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        ReviewResponse response = reviewService.getReview(reviewId);

        // then
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getContent()).isEqualTo("재밌게 봤습니다.");
    }

    @Test
    @DisplayName("리뷰 조회 실패 - 리뷰가 존재하지 않을 때")
    void getReview_fail() {

        // given
        Long reviewId = 1L;

        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.getReview(reviewId));

        // then
        assertEquals(ExceptionCode.REVIEW_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_success() {

        // given
        Long userId = 1L;
        Long reviewId = 1L;

        User user = org.mockito.Mockito.mock(User.class);
        given(user.getId()).willReturn(userId);

        Movie movie = new Movie("영화", "감독", LocalDate.now());
        Review review = new Review(5, "재밌게 봤습니다.", movie, user);

        ReviewUpdateRequest request = new ReviewUpdateRequest(4, "다시 보니까 약간 아쉬웠어요.");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        ReviewResponse response = reviewService.updateReview(reviewId, userId, request);

        // then
        assertThat(response.getRating()).isEqualTo(4);
        assertThat(response.getContent()).isEqualTo("다시 보니까 약간 아쉬웠어요.");
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 리뷰가 존재하지 않을 때")
    void updateReview_fail_1번_케이스() {

        // given
        Long userId = 1L;
        Long reviewId = 1L;

        User user = new User("유저", "user1", "pw", "u1@gmail.com");

        ReviewUpdateRequest request = new ReviewUpdateRequest(4, "다시 보니까 약간 아쉬웠어요.");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.updateReview(reviewId, userId, request));

        // then
        assertEquals(ExceptionCode.REVIEW_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 작성자가 아닐 때")
    void updateReview_fail_2번_케이스() {
        // given
        Long userId = 1L; // 요청자
        Long ownerId = 2L; // 실제 작성자
        Long reviewId = 1L;

        User requester = new User("요청자", "req", "pw", "r@gmail.com");

        User owner = org.mockito.Mockito.mock(User.class);
        given(owner.getId()).willReturn(ownerId);

        Movie movie = new Movie("영화", "감독", LocalDate.now());
        Review review = new Review(5, "재밌게 봤습니다.", movie, owner);

        ReviewUpdateRequest request = new ReviewUpdateRequest(4, "다시 보니까 약간 아쉬웠어요.");

        given(userRepository.findById(userId)).willReturn(Optional.of(requester));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.updateReview(reviewId, userId, request));

        // then
        assertEquals(ExceptionCode.ACCESS_DENIED, exception.getExceptionCode());
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 평점과 내용이 모두 null일 때")
    void updateReview_fail_3번_케이스() {
        // given
        Long userId = 1L;
        Long reviewId = 1L;

        User user = org.mockito.Mockito.mock(User.class);
        given(user.getId()).willReturn(userId);

        Movie movie = new Movie("영화", "감독", LocalDate.now());
        Review review = new Review(5, "재밌게 봤습니다.", movie, user);

        ReviewUpdateRequest request = new ReviewUpdateRequest(null, null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.updateReview(reviewId, userId, request));

        // then
        assertEquals(ExceptionCode.NOTHING_TO_UPDATE, exception.getExceptionCode());
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 내용이 null이 아니지만, 공백일 때")
    void updateReview_fail_4번_케이스() {
        // given
        Long userId = 1L;
        Long reviewId = 1L;

        User user = org.mockito.Mockito.mock(User.class);
        given(user.getId()).willReturn(userId);

        Movie movie = new Movie("영화", "감독", LocalDate.now());
        Review review = new Review(5, "재밌게 봤습니다.", movie, user);

        ReviewUpdateRequest request = new ReviewUpdateRequest(4, " ");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.updateReview(reviewId, userId, request));

        // then
        assertEquals(ExceptionCode.INVALID_REVIEW_CONTENT, exception.getExceptionCode());
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview_success() {
        // given
        Long userId = 1L;
        Long reviewId = 1L;

        User user = org.mockito.Mockito.mock(User.class);
        given(user.getId()).willReturn(userId);

        Movie movie = new Movie("영화", "감독", LocalDate.now());
        Review review = new Review(5, "재밌게 봤습니다.", movie, user);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        reviewService.deleteReview(reviewId, userId);

        // then
        assertThat(review.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("리뷰 삭제 실패 - 리뷰가 존재하지 않을 때")
    void deleteReview_fail_1번_케이스() {
        // given
        Long userId = 1L;
        Long reviewId = 1L;

        User user = new User("유저", "user1", "pw", "u1@gmail.com");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.deleteReview(reviewId, userId));

        // then
        assertEquals(ExceptionCode.REVIEW_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    @DisplayName("리뷰 삭제 실패 - 작성자가 아닐 때")
    void deleteReview_fail_2번_케이스() {
        // given
        Long userId = 1L; // 요청자
        Long ownerId = 2L; // 실제 작성자
        Long reviewId = 1L;

        User requester = new User("요청자", "req", "pw", "r@gmail.com");

        User owner = org.mockito.Mockito.mock(User.class);
        given(owner.getId()).willReturn(ownerId);

        Movie movie = new Movie("영화", "감독", LocalDate.now());
        Review review = new Review(5, "재밌게 봤습니다.", movie, owner);

        given(userRepository.findById(userId)).willReturn(Optional.of(requester));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.deleteReview(reviewId, userId));

        // then
        assertEquals(ExceptionCode.ACCESS_DENIED, exception.getExceptionCode());
    }
}