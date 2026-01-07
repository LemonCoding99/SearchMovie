package com.searchmovie.domain.couponStock.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.domain.couponStock.model.request.CouponStockCreateRequest;
import com.searchmovie.domain.couponStock.model.request.CouponStockUpdateRequest;
import com.searchmovie.domain.couponStock.model.response.CouponStockCreateResponse;
import com.searchmovie.domain.couponStock.model.response.CouponStockGetResponse;
import com.searchmovie.domain.couponStock.model.response.CouponStockUpdateResponse;
import com.searchmovie.domain.couponStock.service.CouponStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponStockController {

    private final CouponStockService couponStockService;

    // 쿠폰 추가
    @PostMapping("/{couponId}/stocks")
    public ResponseEntity<CommonResponse<CouponStockCreateResponse>> createCouponStock(@PathVariable long couponId, @RequestBody CouponStockCreateRequest request) {

        CouponStockCreateResponse result = couponStockService.createCouponStock(couponId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(result, "쿠폰 생성 성공"));
    }

    // 쿠폰 재고 조회
    @GetMapping("/{couponId}/stocks/{couponStockId}")
    public ResponseEntity<CommonResponse<CouponStockGetResponse>> getCouponStock(@PathVariable Long couponStockId) {

        CouponStockGetResponse result = couponStockService.getCouponStock(couponStockId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success(result, "쿠폰 재고 조회 성공"));
    }

    // 쿠폰 수정
    @PatchMapping("/{couponId}/stocks/{couponStockId}")
    public ResponseEntity<CommonResponse<CouponStockUpdateResponse>> updateCouponStock(@PathVariable Long couponStockId, @RequestBody CouponStockUpdateRequest request) {

        CouponStockUpdateResponse result = couponStockService.updateCouponStock(couponStockId, request);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success(result, "쿠폰 재고 조회 성공"));
    }
}