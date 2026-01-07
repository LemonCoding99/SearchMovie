package com.searchmovie.domain.coupon.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.common.model.PageResponse;
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
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

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
