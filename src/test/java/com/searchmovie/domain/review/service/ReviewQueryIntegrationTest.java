package com.searchmovie.domain.review.service;

import com.searchmovie.domain.movie.entity.Movie;
import com.searchmovie.domain.movie.repository.MovieRepository;
import com.searchmovie.domain.review.entity.Review;
import com.searchmovie.domain.review.model.response.ReviewListResponse;
import com.searchmovie.domain.review.model.response.ReviewResponse;
import com.searchmovie.domain.review.repository.ReviewRepository;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ReviewQueryIntegrationTest {

    @Autowired
    ReviewService reviewService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("영화별 리뷰 목록 조회 성공 - 최신순 정렬 + 페이징")
    void getReviews_success_sort_createAt_desc() throws InterruptedException {
        // given
        User user = userRepository.save(new User("유저", "user1", "pw", "u1@gmail.com"));
        Movie movieA = movieRepository.save(new Movie("영화", "감독", LocalDate.now()));
        Movie movieB = movieRepository.save(new Movie("영화2", "감독2", LocalDate.now()));

        reviewRepository.save(new Review(5, "굿굿", movieA, user));
        Thread.sleep(10);
        reviewRepository.save(new Review(3, "쏘쏘", movieA, user));
        reviewRepository.save(new Review(4, "약간 아쉬움", movieB, user));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        ReviewListResponse result = reviewService.getReviews(movieA.getId(), null, pageable);

        // then
        List<ReviewResponse> content = result.getPage().getContent();

        assertThat(content).hasSize(2);
        assertThat(result.getAvgRating()).isEqualTo(4.0);

        assertThat(content.get(0).getCreatedAt())
                .isAfter(content.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("영화별 리뷰 목록 조회 성공 - 평점순 정렬 + 페이징")
    void getReviews_success_sort_rating_desc() {
        // given
        User user = userRepository.save(new User("유저", "user1", "pw", "u1@gmail.com"));
        Movie movie = movieRepository.save(new Movie("영화", "감독", LocalDate.now()));

        reviewRepository.save(new Review(5, "굿굿", movie, user));
        reviewRepository.save(new Review(3, "쏘쏘", movie, user));
        reviewRepository.save(new Review(4, "약간 아쉬움", movie, user));

        Pageable pageable = PageRequest.of(0, 3);

        // when
        ReviewListResponse result = reviewService.getReviews(movie.getId(), "rating,desc", pageable);

        // then
        List<ReviewResponse> content = result.getPage().getContent();

        assertThat(content).hasSize(3);
        assertThat(content.get(0).getRating()).isEqualTo(5);
        assertThat(content.get(1).getRating()).isEqualTo(4);
        assertThat(content.get(2).getRating()).isEqualTo(3);
    }

    @Test
    @DisplayName("영화별 리뷰 목록 조회 성공 - 리뷰가 없을 때 평균 평점 조회")
    void getReviews_success_avgRating() {
        // given
        userRepository.save(new User("유저", "user1", "pw", "u1@gmail.com"));
        Movie movie = movieRepository.save(new Movie("영화", "감독", LocalDate.now()));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        ReviewListResponse result = reviewService.getReviews(movie.getId(), null, pageable);

        // then
        assertThat(result.getAvgRating()).isEqualTo(0.0);
        assertThat(result.getPage().getContent().size()).isEqualTo(0);
    }
}
