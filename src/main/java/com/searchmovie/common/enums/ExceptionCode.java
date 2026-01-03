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
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),



    // movie
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 영화입니다."),
    INVALID_GENRE_NAME(HttpStatus.BAD_REQUEST, "장르 이름이 올바르지 않습니다."),
    MOVIE_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "영화 생성에 실패했습니다."),



    // review
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 영화에 리뷰를 작성했습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
    INVALID_REVIEW_SORT(HttpStatus.BAD_REQUEST, "정렬 조건이 올바르지 않습니다. (createdAt,desc | rating,desc"),
    NOTHING_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 평점 혹은 내용이 없습니다."),
    INVALID_REVIEW_CONTENT(HttpStatus.NOT_FOUND, "리뷰 내용은 공백일 수 없습니다."),

    // search
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    INVALID_SEARCH_PERIOD(HttpStatus.BAD_REQUEST, "검색 범위가 올바르지 않습니다."),
    INVALID_MONTH(HttpStatus.BAD_REQUEST, "검색 월(month)은 1~12 사이여야 합니다."),
    INVALID_YEAR(HttpStatus.BAD_REQUEST, "검색 연도(year)가 허용된 범위를 벗어났습니다.");




    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
