package com.searchmovie.domain.review.repository;

import com.searchmovie.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUser_IdAndMovie_Id(Long userId, Long movieId);

    Page<Review> findByMovie_Id(Long movieId, Pageable sortedPageable);
}
