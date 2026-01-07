package com.searchmovie.domain.coupon.model.response;

import com.searchmovie.domain.coupon.entity.IssuedCouponHistory;
import com.searchmovie.domain.coupon.entity.IssuedCouponStatus;
import com.searchmovie.domain.coupon.model.dto.CouponInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class IssuedCouponHistoryGetDetailResponse {

    private final Long id;
    private final CouponInfo couponInfo;

    private final IssuedCouponStatus status;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiredAt;
    private final LocalDateTime usedAt;

    public static IssuedCouponHistoryGetDetailResponse from(IssuedCouponHistory history) {
        return new IssuedCouponHistoryGetDetailResponse(
                history.getId(),
                CouponInfo.from(history.getCoupon()),
                history.getStatus(),
                history.getIssuedAt(),
                history.getExpiredAt(),
                history.getUsedAt()
        );
    }
}
