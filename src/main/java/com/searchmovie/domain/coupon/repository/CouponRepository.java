package com.searchmovie.domain.coupon.repository;


import com.searchmovie.domain.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByIdAndDeletedAtIsNull(Long id);

    Page<Coupon> findAllByDeletedAtIsNull(Pageable pageable);

}