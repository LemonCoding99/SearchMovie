package com.searchmovie.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // 예외처리 작성 형식
    EXCEPTION_CODE(HttpStatus.OK, "예외처리 메세지"),

    // user




    // movie
    INVALID_GENRE_NAME(HttpStatus.BAD_REQUEST, "장르 이름이 올바르지 않습니다."),
    MOVIE_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "영화 생성에 실패했습니다.");



    // review




    // search









    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
