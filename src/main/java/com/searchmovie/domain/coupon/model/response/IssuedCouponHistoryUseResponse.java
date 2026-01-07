package com.searchmovie.domain.coupon.model.response;

import com.searchmovie.domain.coupon.entity.IssuedCouponHistory;
import com.searchmovie.domain.coupon.entity.IssuedCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class IssuedCouponHistoryUseResponse {

    private final Long id;
    private final Long couponId;
    private final Long userId;
    private final IssuedCouponStatus status;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiredAt;
    private final LocalDateTime usedAt;
    private final LocalDateTime updatedAt;

    public static IssuedCouponHistoryUseResponse from(IssuedCouponHistory history) {
        return new IssuedCouponHistoryUseResponse(
                history.getId(),
                history.getCoupon().getId(),
                history.getUser().getId(),
                history.getStatus(),
                history.getIssuedAt(),
                history.getExpiredAt(),
                history.getUsedAt(),
                history.getUpdatedAt()
        );
    }
}
