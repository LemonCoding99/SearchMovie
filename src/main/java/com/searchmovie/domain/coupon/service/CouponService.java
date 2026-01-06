package com.searchmovie.domain.coupon.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.common.model.PageResponse;
import com.searchmovie.domain.coupon.entity.Coupon;
import com.searchmovie.domain.coupon.model.request.CouponCreateRequest;
import com.searchmovie.domain.coupon.model.request.CouponUpdateRequest;
import com.searchmovie.domain.coupon.model.response.CouponCreateResponse;
import com.searchmovie.domain.coupon.model.response.CouponGetResponse;
import com.searchmovie.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                request.getMaxDiscountPrice(),
                request.getIssueStartAt(),
                request.getIssueEndAt(),
                request.getUsePeriodDays(),
                request.getUseStartAt(),
                request.getUseEndAt()
        );

        Coupon savedCoupon = couponRepository.save(coupon);
        return CouponCreateResponse.of(savedCoupon);
    }

    @Transactional(readOnly = true)
    public CouponGetResponse getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_NOT_FOUND));
        return CouponGetResponse.from(coupon);
    }

    @Transactional(readOnly = true)
    public PageResponse<CouponGetResponse> getCoupons(Pageable pageable) {
        Page<Coupon> couponsPage = couponRepository.findAll(pageable);
        Page<CouponGetResponse> coupons = couponsPage.map(CouponGetResponse::from);
        return PageResponse.from(coupons);
    }

    @Transactional
    public CouponGetResponse updateCoupon(Long couponId, CouponUpdateRequest request) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_NOT_FOUND));
        coupon.update(request.getName(), request.getDiscountRate(), request.getMaxDiscountPrice(), request.getIssueStartAt(),
                request.getIssueEndAt(), request.getUsePeriodDays(), request.getUseStartAt(), request.getUseEndAt());
        return CouponGetResponse.from(coupon);
    }

    @Transactional
    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_NOT_FOUND));
        coupon.softDelete();
    }
}
