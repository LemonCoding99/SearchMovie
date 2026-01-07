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
import com.searchmovie.domain.coupon.model.request.CouponCreateRequest;
import com.searchmovie.domain.coupon.model.request.CouponUpdateRequest;
import com.searchmovie.domain.coupon.model.response.CouponCreateResponse;
import com.searchmovie.domain.coupon.model.response.CouponGetResponse;
import com.searchmovie.domain.coupon.service.CouponService;
import com.searchmovie.domain.user.entity.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
    @Secured(UserRole.Authority.ADMIN)
    @PostMapping
    public ResponseEntity<CommonResponse<CouponCreateResponse>> CreateCoupon(@Valid @RequestBody CouponCreateRequest request) {
        CouponCreateResponse response = couponService.CreateCoupon(request);
        CommonResponse<CouponCreateResponse> commonResponse = new CommonResponse<>(true, "쿠폰 생성 성공", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<CouponGetResponse>> getCoupon(@PathVariable Long id) {
        CouponGetResponse response = couponService.getCoupon(id);
        CommonResponse<CouponGetResponse> commonResponse = new CommonResponse<>(true, "쿠폰 조회 성공", response);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<CouponGetResponse>>> getCoupons(Pageable pageable) {
        PageResponse<CouponGetResponse> response = couponService.getCoupons(pageable);
        CommonResponse<PageResponse<CouponGetResponse>> commonResponse = new CommonResponse<>(true, "쿠폰 전체 조회 성공", response);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Secured(UserRole.Authority.ADMIN)
    @PutMapping("/{couponId}")
    public ResponseEntity<CommonResponse<CouponGetResponse>> updateCoupon(
            @PathVariable Long couponId,
            @Valid @RequestBody CouponUpdateRequest request
    ) {
        CouponGetResponse response = couponService.updateCoupon(couponId, request);
        CommonResponse<CouponGetResponse> commonResponse = new CommonResponse<>(true, "쿠폰 수정 성공", response);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Secured(UserRole.Authority.ADMIN)
    @DeleteMapping("/{couponId}")
    public ResponseEntity<CommonResponse<Void>> deleteCoupon(@PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        CommonResponse<Void> commonResponse = new CommonResponse<>(true, "쿠폰 삭제 성공", null);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
