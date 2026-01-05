package com.searchmovie.domain.auth.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.common.utils.JwtUtil;
import com.searchmovie.domain.auth.model.LoginRequest;
import com.searchmovie.domain.auth.model.LoginResponse;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // 로그인
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findUserByUsername(request.getUsername()).orElseThrow(
                () -> new CustomException(ExceptionCode.USER_NOT_FOUND)
        );

        // 유저 삭제 여부 검사
        if (user.getDeletedAt() != null) {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        }

        String rawPassword = request.getPassword();

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new CustomException(ExceptionCode.INVALID_AUTH_INFO);
        }

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token);
    }
}
