package com.searchmovie.common.exception;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.model.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //커스텀 예외처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse<Void>> customException(CustomException e) {

        ExceptionCode exceptionCode = e.getExceptionCode();

        CommonResponse<Void> response = new CommonResponse<>(false, exceptionCode.getMessage(), null);

        return ResponseEntity.status(exceptionCode.getStatus()).body(response);
    }
}
