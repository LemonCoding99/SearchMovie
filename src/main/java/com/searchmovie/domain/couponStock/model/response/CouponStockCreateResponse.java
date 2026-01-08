package com.searchmovie.domain.couponStock.model.response;

import com.searchmovie.domain.couponStock.entity.CouponStock;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponStockCreateResponse {
    private final long id;
    private final long couponId;
    private final int totalQuantity;
    private final int presentQuantity;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CouponStockCreateResponse(long id, long couponId, int totalQuantity, int presentQuantity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.couponId = couponId;
        this.totalQuantity = totalQuantity;
        this.presentQuantity = presentQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CouponStockCreateResponse from(CouponStock savedCouponStock) {
        return new CouponStockCreateResponse(
                savedCouponStock.getId(),
                savedCouponStock.getCouponId(),
                savedCouponStock.getTotalQuantity(),
                savedCouponStock.getPresentQuantity(),
                savedCouponStock.getCreatedAt(),
                savedCouponStock.getUpdatedAt()
        );
    }
}
