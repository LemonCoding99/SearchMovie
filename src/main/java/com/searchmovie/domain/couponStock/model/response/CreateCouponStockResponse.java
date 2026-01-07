package com.searchmovie.domain.couponStock.model.response;

import com.searchmovie.domain.couponStock.entity.CouponStock;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateCouponStockResponse {
    private final long id;
    private final long couponId;
    private final int totalQuantity;
    private final int presentQuantity;
    private final long version;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CreateCouponStockResponse(long id, long couponId, int totalQuantity, int presentQuantity, long version, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.couponId = couponId;
        this.totalQuantity = totalQuantity;
        this.presentQuantity = presentQuantity;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CreateCouponStockResponse from(CouponStock savedCouponStock) {
        return new CreateCouponStockResponse(
                savedCouponStock.getId(),
                savedCouponStock.getCouponId(),
                savedCouponStock.getTotalQuantity(),
                savedCouponStock.getPresentQuantity(),
                savedCouponStock.getVersion(),
                savedCouponStock.getCreatedAt(),
                savedCouponStock.getUpdatedAt()
        );
    }
}
