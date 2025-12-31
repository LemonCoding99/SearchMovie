package com.searchmovie.common.exception;

import com.searchmovie.common.enums.ExceptionCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ExceptionCode exceptionCode;

    public CustomException(ExceptionCode errorCode) {
        super(errorCode.getMessage());  // RuntimeException 메시지 설정(로그에 남음)
        this.exceptionCode = errorCode;
    }
}
