package com.searchmovie.domain.couponStock.repository;

import com.searchmovie.domain.couponStock.entity.CouponStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {


    Optional<CouponStock> findByCouponId(Long couponId);
}
