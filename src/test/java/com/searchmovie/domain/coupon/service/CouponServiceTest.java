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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;



@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 생성 성공")
    void createCoupon_success() {
        // given
        CouponCreateRequest request = org.mockito.Mockito.mock(CouponCreateRequest.class);

        given(request.getName()).willReturn("테스트쿠폰");
        given(request.getDiscountRate()).willReturn(10);
        given(request.getMaxDiscountPrice()).willReturn(3000);

        LocalDateTime issueStart = LocalDateTime.now().minusDays(1);
        LocalDateTime issueEnd = LocalDateTime.now().plusDays(10);
        given(request.getIssueStartAt()).willReturn(issueStart);
        given(request.getIssueEndAt()).willReturn(issueEnd);

        given(request.getUsePeriodDays()).willReturn(30);
        given(request.getUseStartAt()).willReturn(null);
        given(request.getUseEndAt()).willReturn(null);

        Coupon saved = new Coupon("테스트쿠폰", 10, 3000, issueStart, issueEnd, 30, null, null);
        given(couponRepository.save(any(Coupon.class))).willReturn(saved);

        // when
        CouponCreateResponse response = couponService.createCoupon(request);

        // then
        assertThat(response.getName()).isEqualTo("테스트쿠폰");
        assertThat(response.getDiscountRate()).isEqualTo(10);
        assertThat(response.getUsePeriodDays()).isEqualTo(30);
        assertThat(response.getUseStartAt()).isNull();
        assertThat(response.getUseEndAt()).isNull();
    }

    @Test
    @DisplayName("쿠폰 단건 조회 성공")
    void getCoupon_success() {
        // given
        Long couponId = 1L;

        Coupon coupon = org.mockito.Mockito.mock(Coupon.class);
        given(coupon.getId()).willReturn(couponId);
        given(coupon.getName()).willReturn("조회쿠폰");
        given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));

        // when
        CouponGetResponse response = couponService.getCoupon(couponId);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("조회쿠폰");
    }

    @Test
    @DisplayName("쿠폰 수정 성공")
    void updateCoupon_success() {
        // given
        Long couponId = 1L;

        CouponUpdateRequest request = org.mockito.Mockito.mock(CouponUpdateRequest.class);
        given(request.getName()).willReturn("수정쿠폰");
        given(request.getDiscountRate()).willReturn(15);

        Coupon coupon = org.mockito.Mockito.mock(Coupon.class);
        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));

        given(coupon.getId()).willReturn(couponId);
        given(coupon.getName()).willReturn("수정쿠폰");

        // when
        CouponGetResponse response = couponService.updateCoupon(couponId, request);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("수정쿠폰");
    }

    @Test
    @DisplayName("쿠폰 삭제 성공")
    void deleteCoupon_success() {
        // given
        Long couponId = 1L;

        Coupon coupon = org.mockito.Mockito.mock(Coupon.class);
        given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));

        // when
        couponService.deleteCoupon(couponId);

        // then
        org.mockito.Mockito.verify(coupon).softDelete();
    }

}