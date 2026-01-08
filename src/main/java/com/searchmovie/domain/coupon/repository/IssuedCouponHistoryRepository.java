package com.searchmovie.domain.coupon.repository;

import com.searchmovie.domain.coupon.entity.IssuedCouponHistory;
import com.searchmovie.domain.coupon.entity.IssuedCouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssuedCouponHistoryRepository extends JpaRepository<IssuedCouponHistory, Long> {

    Page<IssuedCouponHistory> findByUser_IdAndDeletedAtIsNull(Long userId, Pageable sortedPageable);

    Page<IssuedCouponHistory> findByUser_IdAndStatusAndDeletedAtIsNull(
            Long userId,
            IssuedCouponStatus status,
            Pageable sortedPageable);

    Optional<IssuedCouponHistory> findByIdAndUser_IdAndDeletedAtIsNull(Long issuedCouponId, Long userId);

    boolean existsByUser_IdAndCoupon_Id(Long userId, Long couponId);
}
