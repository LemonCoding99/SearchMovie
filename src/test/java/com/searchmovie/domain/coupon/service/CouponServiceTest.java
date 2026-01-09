package com.searchmovie.domain.coupon.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.coupon.entity.Coupon;
import com.searchmovie.domain.coupon.entity.IssuedCouponHistory;
import com.searchmovie.domain.coupon.entity.IssuedCouponStatus;
import com.searchmovie.domain.coupon.model.request.CouponCreateRequest;
import com.searchmovie.domain.coupon.model.request.CouponUpdateRequest;
import com.searchmovie.domain.coupon.model.response.CouponCreateResponse;
import com.searchmovie.domain.coupon.model.response.CouponGetResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryIssueResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryUseResponse;
import com.searchmovie.domain.coupon.repository.CouponRepository;
import com.searchmovie.domain.coupon.repository.IssuedCouponHistoryRepository;
import com.searchmovie.domain.couponStock.service.CouponStockService;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IssuedCouponHistoryRepository issuedCouponHistoryRepository;

    @Mock
    private CouponStockService couponStockService;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 발급 성공")
    void issueMyCoupon_success() {

        // given
        Long userId = 1L;
        Long couponId = 1L;

        User user = new User("유저", "user1", "pw", "u1@gmail.com");

        LocalDateTime now = LocalDateTime.now();
        Coupon coupon = new Coupon(
                "블랙 프라이데이 쿠폰",
                30,
                5000,
                now.minusDays(1),
                now.plusDays(1),
                7,
                null,
                null
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));
        given(issuedCouponHistoryRepository.existsByUser_IdAndCoupon_Id(userId, couponId)).willReturn(false);

        // 재고 차감: 재고 서비스에 위임
        willDoNothing().given(couponStockService).decreaseStock(couponId, 1);

        given(issuedCouponHistoryRepository.save(any(IssuedCouponHistory.class)))
                .willAnswer(inv -> inv.getArgument(0));

        // when
        IssuedCouponHistoryIssueResponse response = couponService.issueMyCoupon(userId, couponId);

        // then
        assertThat(response).isNotNull();
        verify(couponStockService).decreaseStock(couponId, 1);
        verify(issuedCouponHistoryRepository).save(any(IssuedCouponHistory.class));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 유저가 존재하지 않을 때")
    void issueMyCoupon_fail_1번_케이스() {

        // given
        Long userId = 1L;
        Long couponId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> couponService.issueMyCoupon(userId, couponId));

        // then
        assertEquals(ExceptionCode.USER_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 쿠폰이 존재하지 않을 때")
    void issueMyCoupon_fail_2번_케이스() {

        // given
        Long userId = 1L;
        Long couponId = 1L;

        User user = new User("유저", "user1", "pw", "u1@gmail.com");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> couponService.issueMyCoupon(userId, couponId));

        // then
        assertEquals(ExceptionCode.COUPON_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 쿠폰 발급 기간이 아닐 때")
    void issueMycoupon_fail_3번_케이스() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        User user = new User("유저", "user1", "pw", "u1@gmail.com");

        LocalDateTime now = LocalDateTime.now();
        Coupon coupon = new Coupon(
                "블랙 프라이데이 쿠폰",
                30,
                5000,
                now.minusDays(10),
                now.minusDays(1),
                7,
                null,
                null
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> couponService.issueMyCoupon(userId, couponId));

        // then
        assertEquals(ExceptionCode.COUPON_ISSUE_PERIOD_INVALID, exception.getExceptionCode());
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 이미 발급한 쿠폰일 때")
    void issueMyCoupon_fail_4번_케이스() {

        // given
        Long userId = 1L;
        Long couponId = 1L;

        User user = new User("유저", "user1", "pw", "u1@gmail.com");

        LocalDateTime now = LocalDateTime.now();
        Coupon coupon = new Coupon(
                "블랙 프라이데이 쿠폰",
                30,
                5000,
                now.minusDays(1),
                now.plusDays(1),
                7,
                null,
                null
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(couponRepository.findByIdAndDeletedAtIsNull(couponId)).willReturn(Optional.of(coupon));
        given(issuedCouponHistoryRepository.existsByUser_IdAndCoupon_Id(userId, couponId)).willReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> couponService.issueMyCoupon(userId, couponId));

        // then
        assertEquals(ExceptionCode.COUPON_ALREADY_ISSUED, exception.getExceptionCode());
    }

    @Test
    @DisplayName("내 쿠폰 사용 성공")
    void useMycoupon_success() {

        // given
        Long userId = 1L;
        Long issuedCouponId = 1L;

        User user = new User("유저", "user1", "pw", "u1@gmail.com");

        LocalDateTime now = LocalDateTime.now();

        Coupon coupon = new Coupon(
                "블랙 프라이데이 쿠폰",
                30,
                5000,
                now.minusDays(1),
                now.plusDays(1),
                7,
                null,
                null
        );

        IssuedCouponHistory history = new IssuedCouponHistory(
                user,
                coupon,
                now.minusDays(1),   // issuedAt
                now.plusDays(6)     // expiredAt
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        given(issuedCouponHistoryRepository.findByIdAndUser_IdAndDeletedAtIsNull(issuedCouponId, userId))
                .willReturn(Optional.of(history));

        // when
        IssuedCouponHistoryUseResponse response = couponService.useMyCoupon(userId, issuedCouponId);

        // then
        assertThat(response).isNotNull();
        assertThat(history.getStatus()).isEqualTo(IssuedCouponStatus.USED);
        assertThat(history.getUsedAt()).isNotNull();
    }

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
        verify(coupon).softDelete();
    }
}