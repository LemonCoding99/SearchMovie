package com.searchmovie.domain.couponStock.repository;

import com.searchmovie.domain.couponStock.entity.CouponStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {
    Optional<CouponStock> findByCouponId(Long couponId);

    boolean existsByCouponId(long couponId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT cs
            FROM CouponStock cs
            WHERE cs.couponId = :couponId
            
            """)
    Optional<CouponStock> findByIdForLOCK(@Param("couponId") Long couponId);
}
