package com.searchmovie.common.exception;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.model.CommonResponse;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(SearchException.class)
    public ResponseEntity<CommonResponse<Void>> securityException(SearchException e) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        CommonResponse<Void> response = new CommonResponse<>(false, exceptionCode.getMessage(), null);
        return ResponseEntity.status(exceptionCode.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception e) {
        CommonResponse<Void> response = new CommonResponse<>(false, ExceptionCode.INTERNAL_SERVER_ERROR.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


}
