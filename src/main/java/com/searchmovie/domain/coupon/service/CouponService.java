package com.searchmovie.domain.coupon.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.common.model.PageResponse;
import com.searchmovie.domain.coupon.entity.Coupon;
import com.searchmovie.domain.coupon.entity.IssuedCouponHistory;
import com.searchmovie.domain.coupon.entity.IssuedCouponStatus;
import com.searchmovie.domain.coupon.model.request.CouponCreateRequest;
import com.searchmovie.domain.coupon.model.request.CouponUpdateRequest;
import com.searchmovie.domain.coupon.model.response.CouponCreateResponse;
import com.searchmovie.domain.coupon.model.response.CouponGetResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryGetDetailResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryIssueResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryUseResponse;
import com.searchmovie.domain.coupon.repository.CouponRepository;
import com.searchmovie.domain.coupon.repository.IssuedCouponHistoryRepository;
import com.searchmovie.domain.couponStock.service.CouponStockService;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponHistoryRepository issuedCouponHistoryRepository;
    private final UserRepository userRepository;
    private final CouponStockService couponStockService;

    // 쿠폰 발급: 발급 기간/중복 발급 검증 후 재고 차감 및 발급 기록 생성
    @Transactional
    public IssuedCouponHistoryIssueResponse issueMyCoupon(Long userId, Long couponId) {

        User foundUser = getUserOrThrow(userId);
        Coupon foundCoupon = getCouponOrThrow(couponId);

        validateIssueStartAtAndIssueEndAt(foundCoupon);
        validateDuplicateIssue(userId, couponId);

        // 쿠폰 수량 차감
        couponStockService.decreaseStock(couponId, 1);

        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiredAt = calculateExpiredAt(foundCoupon, issuedAt);

        IssuedCouponHistory history = new IssuedCouponHistory(foundUser, foundCoupon, issuedAt, expiredAt);
        IssuedCouponHistory saved = issuedCouponHistoryRepository.save(history);

        return IssuedCouponHistoryIssueResponse.from(saved);
    }

    // 내 쿠폰 조회: 상태별 필터링 및 발급일 기준 페이징 조회
    @Transactional(readOnly = true)
    public PageResponse<IssuedCouponHistoryGetDetailResponse> getMyCouponsDetail(
            Long userId,
            IssuedCouponStatus status,
            Pageable pageable) {

        // 유저 존재 검증용
        getUserOrThrow(userId);

        // status가 없으면 전체, 있으면 상태별 필터링
        Page<IssuedCouponHistory> historyPage = (status == null)
                ? issuedCouponHistoryRepository.findByUser_IdAndDeletedAtIsNull(userId, pageable)
                : issuedCouponHistoryRepository.findByUser_IdAndStatusAndDeletedAtIsNull(userId, status, pageable);

        // 엔티티 -> 응답 DTO 변환
        Page<IssuedCouponHistoryGetDetailResponse> dtoPage =
                historyPage.map(IssuedCouponHistoryGetDetailResponse::from);

        return PageResponse.from(dtoPage);
    }

    // 쿠폰 사용: 본인 쿠폰 여부 및 사용 가능 조건 검증 후 사용 처리
    @Transactional
    public IssuedCouponHistoryUseResponse useMyCoupon(Long userId, Long issuedCouponId) {

        // 유저 존재 검증용
        getUserOrThrow(userId);
        IssuedCouponHistory foundIssuedCoupon = getMyIssuedCouponOrThrow(issuedCouponId, userId);

        validateNotUsed(foundIssuedCoupon);

        LocalDateTime now = LocalDateTime.now();
        validateNotExpired(foundIssuedCoupon, now);
        validateStarted(foundIssuedCoupon, now);

        // 사용 처리
        foundIssuedCoupon.use(now);

        return IssuedCouponHistoryUseResponse.from(foundIssuedCoupon);
    }

    @Transactional
    public CouponCreateResponse createCoupon(CouponCreateRequest request) {

        boolean hasPeriodDays = request.getUsePeriodDays() != null;
        boolean hasUseAt = request.getUseStartAt() != null || request.getUseEndAt() != null;

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
        Page<Coupon> couponsPage = couponRepository.findAllByDeletedAtIsNull(pageable);
        Page<CouponGetResponse> coupons = couponsPage.map(CouponGetResponse::from);
        return PageResponse.from(coupons);
    }

    @Transactional
    public CouponGetResponse updateCoupon(Long couponId, CouponUpdateRequest request) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_NOT_FOUND));

        coupon.update(
                request.getName(),
                request.getDiscountRate(),
                request.getMaxDiscountPrice(),
                request.getIssueStartAt(),
                request.getIssueEndAt(),
                request.getUsePeriodDays(),
                request.getUseStartAt(),
                request.getUseEndAt()
        );

        return CouponGetResponse.from(coupon);
    }

    @Transactional
    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_NOT_FOUND));
        coupon.softDelete();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }

    private Coupon getCouponOrThrow(Long couponId) {
        return couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_NOT_FOUND));
    }

    // 발급 기간 확인
    private void validateIssueStartAtAndIssueEndAt(Coupon coupon) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getIssueStartAt()) || now.isAfter(coupon.getIssueEndAt())) {
            throw new CustomException(ExceptionCode.COUPON_ISSUE_PERIOD_INVALID);
        }
    }

    // 중복 발급 확인
    private void validateDuplicateIssue(Long userId, Long couponId) {
        if (issuedCouponHistoryRepository.existsByUser_IdAndCoupon_Id(userId, couponId)) {
            throw new CustomException(ExceptionCode.COUPON_ALREADY_ISSUED);
        }
    }

    // 만료일 계산
    private LocalDateTime calculateExpiredAt(Coupon coupon, LocalDateTime issuedAt) {

        Integer periodDays = coupon.getUsePeriodDays();
        LocalDateTime useStartAt = coupon.getUseStartAt();
        LocalDateTime useEndAt = coupon.getUseEndAt();

        // 발급 후 N일(상대 기간)
        if (periodDays != null) {
            // 고정 기간 값이 같이 있으면 정책 오류
            if (useStartAt != null || useEndAt != null) {
                throw new CustomException(ExceptionCode.COUPON_POLICY_INVALID);
            }
            return issuedAt.plusDays(periodDays);
        }

        // 고정 기간: start/end 둘 다 있어야 함
        if (useStartAt == null && useEndAt == null) {
            throw new CustomException(ExceptionCode.COUPON_POLICY_INVALID);
        }
        return useEndAt;
    }

    // 내 쿠폰인지 조회
    private IssuedCouponHistory getMyIssuedCouponOrThrow(Long issuedCouponId, Long userId) {
        return issuedCouponHistoryRepository
                .findByIdAndUser_IdAndDeletedAtIsNull(issuedCouponId, userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ISSUED_COUPON_NOT_FOUND));
    }

    // 이미 사용한 쿠폰인지 확인
    private void validateNotUsed(IssuedCouponHistory history) {
        if (history.getStatus() == IssuedCouponStatus.USED) {
            throw new CustomException(ExceptionCode.COUPON_ALREADY_USED);
        }
    }

    // 만료된 쿠폰
    private void validateNotExpired(IssuedCouponHistory history, LocalDateTime now) {
        if (history.getExpiredAt().isBefore(now)) {
            throw new CustomException(ExceptionCode.COUPON_EXPIRED);
        }
    }

    // 사용 시작 전 쿠폰
    private void validateStarted(IssuedCouponHistory history, LocalDateTime now) {

        LocalDateTime useStartAt = history.getCoupon().getUseStartAt();

        if (useStartAt != null && useStartAt.isAfter(now)) {
            throw new CustomException(ExceptionCode.COUPON_NOT_STARTED);
        }
    }
}
