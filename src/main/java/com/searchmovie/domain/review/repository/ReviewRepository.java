package com.searchmovie.domain.review.repository;

import com.searchmovie.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUser_IdAndMovie_Id(Long userId, Long movieId);

    Page<Review> findByMovie_Id(Long movieId, Pageable sortedPageable);

    @Query("SELECT avg(r.rating) FROM Review r WHERE r.movie.id = :movieId AND r.deletedAt IS NULL")
    Double findAvgRatingByMovie_Id(Long movieId);
}
