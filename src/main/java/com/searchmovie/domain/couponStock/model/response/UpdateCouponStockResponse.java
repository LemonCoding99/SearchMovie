package com.searchmovie.domain.couponStock.model.response;

import com.searchmovie.domain.couponStock.entity.CouponStock;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateCouponStockResponse {
    private final long id;
    private final long couponId;
    private final int totalQuantity;
    private final int presentQuantity;
    private final long version;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public UpdateCouponStockResponse(long id, long couponId, int totalQuantity, int presentQuantity, long version, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.couponId = couponId;
        this.totalQuantity = totalQuantity;
        this.presentQuantity = presentQuantity;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UpdateCouponStockResponse from(CouponStock couponStock) {
        return new UpdateCouponStockResponse(
                couponStock.getId(),
                couponStock.getCouponId(),
                couponStock.getTotalQuantity(),
                couponStock.getPresentQuantity(),
                couponStock.getVersion(),
                couponStock.getCreatedAt(),
                couponStock.getUpdatedAt()
        );
    }
}
