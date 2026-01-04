package com.searchmovie.common.exception;

import com.searchmovie.common.enums.ExceptionCode;
import lombok.Getter;

@Getter
public class SearchException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public SearchException(ExceptionCode errorCode) {
        super(errorCode.getMessage());
        this.exceptionCode = errorCode;
    }
}
