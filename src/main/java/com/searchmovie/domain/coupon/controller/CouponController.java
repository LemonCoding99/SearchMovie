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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.searchmovie.domain.coupon.model.request.CouponCreateRequest;
import com.searchmovie.domain.coupon.model.request.CouponUpdateRequest;
import com.searchmovie.domain.coupon.model.response.CouponCreateResponse;
import com.searchmovie.domain.coupon.model.response.CouponGetResponse;
import com.searchmovie.domain.user.entity.UserRole;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/coupons/{couponId}/issue")
    public ResponseEntity<CommonResponse<IssuedCouponHistoryIssueResponse>> issueCoupon(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long couponId) {

        IssuedCouponHistoryIssueResponse result = couponService.issueMyCoupon(userId, couponId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(result, "쿠폰 발급 성공"));
    }

    @GetMapping("/users/me/coupons")
    public ResponseEntity<CommonResponse<PageResponse<IssuedCouponHistoryGetDetailResponse>>> getMyCouponsDetail(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) IssuedCouponStatus status,
            @PageableDefault(page = 0, size = 10, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<IssuedCouponHistoryGetDetailResponse> result = couponService.getMyCouponsDetail(userId, status, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success(result, "내 쿠폰 조회 성공"));
    }

    @PatchMapping("/users/me/coupons/{issuedCouponId}/use")
    public ResponseEntity<CommonResponse<IssuedCouponHistoryUseResponse>> useMyCoupon(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long issuedCouponId) {

        IssuedCouponHistoryUseResponse result = couponService.useMyCoupon(userId, issuedCouponId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success(result, "쿠폰 사용 처리 성공"));

    }


    // 쿠폰 생성
    @Secured(UserRole.Authority.ADMIN)
    @PostMapping("/coupons")
    public ResponseEntity<CommonResponse<CouponCreateResponse>> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        CouponCreateResponse result = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(result, "쿠폰 생성 성공"));
    }

    // 쿠폰 조회
    @GetMapping("/coupons/{id}")
    public ResponseEntity<CommonResponse<CouponGetResponse>> getCoupon(@PathVariable Long id) {
        CouponGetResponse result = couponService.getCoupon(id);
        return ResponseEntity.ok(CommonResponse.success(result, "쿠폰 조회 성공"));
    }

    // 쿠폰 전체 조회
    @GetMapping("/coupons")
    public ResponseEntity<CommonResponse<PageResponse<CouponGetResponse>>> getCoupons(Pageable pageable) {
        PageResponse<CouponGetResponse> result = couponService.getCoupons(pageable);
        return ResponseEntity.ok(CommonResponse.success(result, "쿠폰 전체 조회 성공"));
    }


    // 쿠폰 수정
    @Secured(UserRole.Authority.ADMIN)
    @PutMapping("/coupons/{couponId}")
    public ResponseEntity<CommonResponse<CouponGetResponse>> updateCoupon(
            @PathVariable Long couponId,
            @Valid @RequestBody CouponUpdateRequest request
    ) {
        CouponGetResponse result = couponService.updateCoupon(couponId, request);
        return ResponseEntity.ok(CommonResponse.success(result, "쿠폰 수정 성공"));
    }

    // 쿠폰 삭제
    @Secured(UserRole.Authority.ADMIN)
    @DeleteMapping("/coupons/{couponId}")
    public ResponseEntity<CommonResponse<Void>> deleteCoupon(@PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.ok(CommonResponse.success(null, "쿠폰 삭제 성공"));
    }
}
