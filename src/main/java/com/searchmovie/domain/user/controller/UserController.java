package com.searchmovie.domain.user.controller;

import com.searchmovie.common.model.CommonResponse;
import com.searchmovie.domain.user.dto.request.UserCreateRequest;
import com.searchmovie.domain.user.dto.request.UserUpdateRequest;
import com.searchmovie.domain.user.dto.response.UserCreateResponse;
import com.searchmovie.domain.user.dto.response.UserGetResponse;
import com.searchmovie.domain.user.dto.response.UserUpdateResponse;
import com.searchmovie.domain.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/auth/signup")
    public ResponseEntity<CommonResponse<UserCreateResponse>> signup(@Valid @RequestBody UserCreateRequest request) {

        UserCreateResponse result = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(result, "회원가입이 완료되었습니다"));
    }

    // 내 정보 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<CommonResponse<UserGetResponse>> getUserOwn(@PathVariable Long userId) {

        UserGetResponse result = userService.getUserOwn(userId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success(result, "내 정보 조회 성공"));
    }

    // 내 정보 수정
    @PutMapping("/users/{userId}")
    public ResponseEntity<CommonResponse<UserUpdateResponse>> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {

        UserUpdateResponse result = userService.updateUser(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success(result, "사용자 정보가 수정되었습니다."));
    }

    // 회원 탈퇴
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<CommonResponse<Void>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(CommonResponse.success(null,"회원 탈퇴가 완료되었습니다."));
    }
}
