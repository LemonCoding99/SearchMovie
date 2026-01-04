package com.searchmovie.domain.auth.service;

import com.searchmovie.common.utils.JwtUtil;
import com.searchmovie.domain.auth.dto.LoginRequest;
import com.searchmovie.domain.auth.dto.LoginResponse;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
                () -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.")
        );

        String rawPassword = request.getPassword();

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token);
    }
}
