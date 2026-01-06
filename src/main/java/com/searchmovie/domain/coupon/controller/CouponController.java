package com.searchmovie.domain.coupon.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.domain.coupon.model.request.CouponCreateRequest;
import com.searchmovie.domain.coupon.model.response.CouponCreateResponse;
import com.searchmovie.domain.coupon.model.response.CouponGetResponse;
import com.searchmovie.domain.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CommonResponse<CouponCreateResponse>> CreateCoupon(@Valid @RequestBody CouponCreateRequest request) {
        CouponCreateResponse response = couponService.CreateCoupon(request);
        CommonResponse<CouponCreateResponse> commonResponse = new CommonResponse<>(true, "쿠폰 생성 성공", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<>> getCoupon(@PathVariable Long id) {
        CouponGetResponse response = couponService.getCoupon(id);
        CommonResponse<CouponGetResponse> commonResponse = new CommonResponse<>(true, "쿠폰 조회 성공", response);
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }


}
