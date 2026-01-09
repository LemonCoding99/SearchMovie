package com.searchmovie.domain.auth.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.common.utils.JwtUtil;
import com.searchmovie.domain.auth.model.LoginRequest;
import com.searchmovie.domain.auth.model.LoginResponse;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    private User testUser;
    private String rawPassword = "Password123!";
    private String encodedPassword = "{bcrypt}$2b$12$mvnmEHGcl2KqJg/8udGbjek/katAUSctc5gxuMpBL6105tkrCrwHG";

    @BeforeEach
    void setup() {
        // raw password = Password123!
        testUser = new User(
                "testname",
                "testUserName",
                encodedPassword,
                "test@example.com"
        );
        ReflectionTestUtils.setField(testUser, "id", 1L);

    }

    @Test
    void login_success() {

        // given
        LoginRequest request = mock(LoginRequest.class);
        when(request.getUsername()).thenReturn("testname");
        when(request.getPassword()).thenReturn(rawPassword);

        when(userRepository.findUserByUsername("testname")).thenReturn(Optional.of(testUser));

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        when(jwtUtil.generateToken(testUser))
                .thenReturn("mockToken");

        // when
        LoginResponse response = authService.login(request);

        // then
        assertNotNull(response);
        verify(userRepository).findUserByUsername("testname");
        verify(passwordEncoder).matches(rawPassword, testUser.getPassword());
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    @DisplayName("로그인 실패 - 로그인하고자 하는 유저가 존재하지 않을 때")
    void login_failed_v1() {

        // given
        LoginRequest request = mock(LoginRequest.class);
        when(request.getUsername()).thenReturn("wrongtestname");

        when(userRepository.findUserByUsername(request.getUsername())).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> authService.login(request)
        );

        // then
        assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("로그인 실패 - 인증 정보가 다를 경우 (아이디 비번 틀림)")
    void login_failed_v2() {

        // given
        LoginRequest request = mock(LoginRequest.class);
        when(request.getUsername()).thenReturn("testname");
        when(request.getPassword()).thenReturn("wrongPassword123!");

        when(userRepository.findUserByUsername("testname")).thenReturn(Optional.of(testUser));

        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> authService.login(request)
        );

        // then
        assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.INVALID_AUTH_INFO);
    }
}