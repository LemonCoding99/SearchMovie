package com.searchmovie.domain.coupon.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.common.model.PageResponse;
import com.searchmovie.domain.coupon.entity.IssuedCouponStatus;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryGetDetailResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryIssueResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryUseResponse;
import com.searchmovie.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CouponController {

    private final CouponService couponService;

    // 쿠폰 발급
    @PostMapping("/coupons/{couponId}/issue")
    public ResponseEntity<CommonResponse<IssuedCouponHistoryIssueResponse>> issueCoupon(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long couponId) {

        IssuedCouponHistoryIssueResponse result = couponService.issueMyCoupon(userId, couponId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(result, "쿠폰 발급 성공"));
    }

    // 내 쿠폰 목록 조회
    @GetMapping("/users/me/coupons")
    public ResponseEntity<CommonResponse<PageResponse<IssuedCouponHistoryGetDetailResponse>>> getMyCouponsDetail(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) IssuedCouponStatus status,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        PageResponse<IssuedCouponHistoryGetDetailResponse> result = couponService.getMyCouponsDetail(userId, status, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success(result, "내 쿠폰 조회 성공"));
    }

    // 쿠폰 사용
    @PatchMapping("/users/me/coupons/{issuedCouponId}/use")
    public ResponseEntity<CommonResponse<IssuedCouponHistoryUseResponse>> useMyCoupon(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long issuedCouponId) {

        IssuedCouponHistoryUseResponse result = couponService.useMyCoupon(userId, issuedCouponId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success(result, "쿠폰 사용 처리 성공"));
    }
}
