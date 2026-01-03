package com.searchmovie.domain.auth.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.domain.auth.dto.LoginRequest;
import com.searchmovie.domain.auth.dto.LoginResponse;
import com.searchmovie.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // login
    @PostMapping("/api/auth/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {

        LoginResponse result = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success(result, "로그인 성공"));
    }
}