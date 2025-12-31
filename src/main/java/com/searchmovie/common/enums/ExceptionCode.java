package com.searchmovie.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // 예외처리 작성 형식
    EXCEPTION_CODE(HttpStatus.OK, "예외처리 메세지");

    // user




    // movie




    // review




    // search









    private final HttpStatus status;
    private final String message;

    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
