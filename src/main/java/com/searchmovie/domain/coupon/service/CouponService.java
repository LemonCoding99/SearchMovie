package com.searchmovie.domain.coupon.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.coupon.entity.Coupon;
import com.searchmovie.domain.coupon.model.request.CouponCreateRequest;
import com.searchmovie.domain.coupon.model.response.CouponCreateResponse;
import com.searchmovie.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public CouponCreateResponse CreateCoupon(CouponCreateRequest request) {
        // 사용기간 정책 검증  둘 중에 하나는 있어야되며, 두 개 다 null이거나 두개 다 값이 있으면 안됨
        boolean hasPeriodDays = request.getUsePeriodDays() != null;
        boolean hasUseAt =
                request.getUseStartAt() != null || request.getUseEndAt() != null;

        // 둘 다 없으면 안 됨
        if (!hasPeriodDays && !hasUseAt) {
            throw new CustomException(ExceptionCode.INVALID_COUPON_USE_POLICY);
        }

        // 둘 다 있으면 안 됨
        if (hasPeriodDays && hasUseAt) {
            throw new CustomException(ExceptionCode.INVALID_COUPON_USE_POLICY);
        }

        Coupon coupon = new Coupon(
                request.getName(),
                request.getDiscountRate(),
                request.getIssueStartAt(),
                request.getIssueEndAt(),
                request.getUsePeriodDays(),
                request.getUseStartAt(),
                request.getUseEndAt()
        );

        Coupon savedCoupon = couponRepository.save(coupon);
        return CouponCreateResponse.of(savedCoupon);
    }
}
