package com.searchmovie.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // 예외처리 작성 형식
    EXCEPTION_CODE(HttpStatus.OK, "예외처리 메세지"),

    // 공통
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 리소스에 대한 권한이 없습니다."),

    // user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // auth
    INVALID_AUTH_INFO(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    MISSING_REQUIRED_AUTH_FIELD(HttpStatus.BAD_REQUEST, "입력되지 않은 필드가 존재합니다."),
    MISSING_REQUIRED_LOGIN_FIELD(HttpStatus.BAD_REQUEST, "username 또는 password가 입력되지않았습니다."),
    REQUIRED_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    NOT_HAVE_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    ALREADY_IN_USING_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),

    // movie
    INVALID_GENRE_NAME(HttpStatus.BAD_REQUEST, "장르 이름이 올바르지 않습니다."),
    MOVIE_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "영화 생성에 실패했습니다."),
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 영화입니다."),
    INVALID_MOVIE_TITLE(HttpStatus.BAD_REQUEST, "영화 제목이 올바르지 않습니다."),
    INVALID_MOVIE_DIRECTOR(HttpStatus.BAD_REQUEST, "감독 이름이 올바르지 않습니다."),
    INVALID_MOVIE_RELEASE_DATE(HttpStatus.BAD_REQUEST, "개봉일이 올바르지 않습니다."),

    // review
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 영화에 리뷰를 작성했습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
    INVALID_REVIEW_SORT(HttpStatus.BAD_REQUEST, "정렬 조건이 올바르지 않습니다. (createdAt,desc | rating,desc"),
    NOTHING_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 평점 혹은 내용이 없습니다."),
    INVALID_REVIEW_CONTENT(HttpStatus.NOT_FOUND, "리뷰 내용은 공백일 수 없습니다."),

    // search
    SEARCH_KEYWORD_REQUIRED(HttpStatus.NO_CONTENT, "검색 키워드는 필수입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    INVALID_SEARCH_PERIOD(HttpStatus.BAD_REQUEST, "검색 범위가 올바르지 않습니다."),
    INVALID_MONTH(HttpStatus.BAD_REQUEST, "검색 월(month)은 1~12 사이여야 합니다."),
    INVALID_YEAR(HttpStatus.BAD_REQUEST, "검색 연도(year)가 허용된 범위를 벗어났습니다."),

    // coupon
    ISSUED_COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 발급 쿠폰입니다."),
    COUPON_ALREADY_USED(HttpStatus.CONFLICT, "이미 사용한 쿠폰입니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "이미 만료된 쿠폰입니다."),
    COUPON_NOT_STARTED(HttpStatus.BAD_REQUEST, "아직 사용 가능한 기간이 아닙니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 쿠폰입니다."),
    COUPON_ISSUE_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "쿠폰 발급 기간이 아닙니다."),
    COUPON_ALREADY_ISSUED(HttpStatus.CONFLICT, "이미 발급받은 쿠폰입니다."),
    COUPON_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰 재고가 없습니다."),
    COUPON_OUT_OF_STOCK(HttpStatus.CONFLICT, "쿠폰 수량이 소진되었습니다."),
    INVALID_COUPON_USE_POLICY(HttpStatus.BAD_REQUEST, "쿠폰 사용기간 정책이 올바르지 않습니다."),
    INVALID_COUPON_DISCOUNT(HttpStatus.BAD_REQUEST, "쿠폰 할인 정책이 올바르지 않습니다."),



    // couponStock
    COUPONSTOCK_NOT_FOUND(HttpStatus.NO_CONTENT, "존재하지 않는 쿠폰이거나 쿠폰 재고 정보가 없습니다."),
    ALREADY_EXISTS_COUPONSTOCK(HttpStatus.CONFLICT, "이미 존재하는 쿠폰 재고입니다.");


    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
